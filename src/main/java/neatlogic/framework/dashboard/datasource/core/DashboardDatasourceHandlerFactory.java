package neatlogic.framework.dashboard.datasource.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dashboard.datasource.exception.DashboardDatasourceTypeNotSupportedException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** 大屏数据源注册工厂，随模块初始化收集 Handler，避免跨模块直接依赖实现类。 */
@RootComponent
public class DashboardDatasourceHandlerFactory extends ModuleInitializedListenerBase {
    private static final String DEFAULT_TYPE = "datawarehouse";
    private static final ConcurrentMap<String, IDashboardDatasourceHandler> handlerMap = new ConcurrentHashMap<>();

    /** 仅缺省、null 和空白类型兼容旧大屏；其他非空类型精确匹配，禁止静默回退。 */
    public static IDashboardDatasourceHandler getHandler(String type) {
//        String type = StringUtils.isBlank(datasourceType) ? DEFAULT_TYPE : datasourceType;
        IDashboardDatasourceHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new DashboardDatasourceTypeNotSupportedException(type);
        }
        return handler;
    }

    /** 基于注册快照构造独立选项，数据仓库优先，其余按类型排序；名称按本次请求语言解析。 */
    public static List<JSONObject> getTypeList() {
        List<IDashboardDatasourceHandler> handlers = new ArrayList<>(handlerMap.values());
        handlers.sort(Comparator.comparing((IDashboardDatasourceHandler handler) -> !DEFAULT_TYPE.equals(handler.getType()))
                .thenComparing(IDashboardDatasourceHandler::getType));
        List<JSONObject> result = new ArrayList<>();
        for (IDashboardDatasourceHandler handler : handlers) {
            JSONObject option = new JSONObject();
            option.put("value", handler.getType());
            option.put("text", handler.getTypeName());
            result.add(option);
        }
        return result;
    }

    /** 每个模块刷新后追加注册其 Spring Bean，同一实例重复事件保持幂等。 */
    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IDashboardDatasourceHandler> beans = context.getBeansOfType(IDashboardDatasourceHandler.class);
        for (Map.Entry<String, IDashboardDatasourceHandler> entry : beans.entrySet()) {
            IDashboardDatasourceHandler handler = entry.getValue();
            String type = handler.getType();
            if (StringUtils.isBlank(type)) {
                throw new IllegalStateException("大屏数据源 Handler 类型不能为空，模块：" + context.getId() + "，Bean：" + entry.getKey());
            }
            // 原子注册避免模块并发加载时互相覆盖；只允许同一个 Spring Bean 重复注册。
            IDashboardDatasourceHandler existing = handlerMap.putIfAbsent(type, handler);
            if (existing != null && existing != handler) {
                throw new IllegalStateException("大屏数据源类型已注册：" + type + "，模块：" + context.getId() + "，Bean：" + entry.getKey());
            }
        }
    }

    /** root-context 初始化时子模块尚未就绪，统一等待模块刷新事件完成注册。 */
    @Override
    protected void myInit() {

    }
}
