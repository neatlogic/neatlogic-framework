package neatlogic.module.framework.dashboard.datasource.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dashboard.datasource.core.IDashboardDatasourceHandler;
import neatlogic.framework.exception.integration.IntegrationHandlerNotFoundException;
import neatlogic.framework.exception.integration.IntegrationNotFoundException;
import neatlogic.framework.exception.integration.IntegrationRequestResultIsEmptyException;
import neatlogic.framework.exception.integration.IntegrationUnActiveException;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.integration.core.IIntegrationHandler;
import neatlogic.framework.integration.core.IntegrationAuthorityChecker;
import neatlogic.framework.integration.core.IntegrationDefaultValueUtil;
import neatlogic.framework.integration.core.IntegrationHandlerFactory;
import neatlogic.framework.integration.core.IntegrationPatternUtil;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import neatlogic.framework.util.$;
import neatlogic.framework.util.TableResultUtil;
import neatlogic.module.framework.integration.handler.FrameworkRequestFrom;
import neatlogic.framework.dashboard.datasource.exception.DashboardIntegrationConfigInvalidException;
import neatlogic.framework.dashboard.datasource.exception.DashboardIntegrationRequestFailedException;
import neatlogic.module.framework.dashboard.integration.handler.DashboardIntegrationHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 将大屏组件的统一数据源契约适配到已启用的大屏集成配置，不暴露集成密钥和地址。 */
@Component
public class IntegrationDashboardDatasourceHandler implements IDashboardDatasourceHandler {
    private static final String HANDLER = DashboardIntegrationHandler.class.getSimpleName();

    @Resource
    private IntegrationMapper integrationMapper;

    /** framework 模块初始化后由公共数据源工厂自动注册。 */
    @Override
    public String getType() {
        return "integration";
    }

    /** 按当前请求语言展示数据源类型名称，保持类型标识用于查询分发。 */
    @Override
    public String getTypeName() {
        return $.t("nmfddh.integrationdashboarddatasourcehandler.gettypename");
    }

    /** 固定启用状态和规范，回显不分页且按实际命中计数，普通查询保留原分页。 */
    @Override
    public JSONObject listDatasource(JSONObject params) {
        JSONArray selected = params.getJSONArray("defaultValue");
        if (CollectionUtils.isEmpty(selected)) {
            selected = params.getJSONArray("idList");
        }
        final boolean exactMatch = CollectionUtils.isNotEmpty(selected);
        // BasePageVo.getNeedPage 固定返回 true，局部覆盖以确保精确回显的 SQL 确实不带 LIMIT。
        IntegrationVo query = new IntegrationVo() {
            @Override
            public Boolean getNeedPage() {
                return !exactMatch;
            }
        };
        query.setHandler(HANDLER);
        query.setIsActive(1);
        query.setKeyword(params.getString("keyword"));
        if (params.getInteger("currentPage") != null && params.getInteger("currentPage") > 0) {
            query.setCurrentPage(params.getInteger("currentPage"));
        }
        query.setPageSize(params.getInteger("pageSize"));
        if (exactMatch) {
            query.setDefaultValue(selected);
        }
        List<ValueTextVo> options = integrationMapper.searchIntegrationForSelect(query);
        List<JSONObject> rows = new ArrayList<>();
        if (options != null) {
            for (ValueTextVo option : options) {
                JSONObject row = new JSONObject();
                row.put("id", option.getValue());
                row.put("label", option.getText());
                rows.add(row);
            }
        }
        query.setRowNum(exactMatch ? rows.size() : integrationMapper.searchIntegrationCount(query));
        return TableResultUtil.getResult(rows, query);
    }

    /** 字段只来自已启用的输出表头默认值；详情不存在仍返回 null，不执行集成请求。 */
    @Override
    public JSONObject getDatasource(JSONObject params) {
        IntegrationVo integration = getIntegration(params.getString("id"));
        if (integration == null) {
            return null;
        }
        JSONArray headers = getTheadList(integration, getIntegrationHandler());
        JSONArray fields = new JSONArray();
        for (int i = 0; i < headers.size(); i++) {
            JSONObject header = headers.getJSONObject(i);
            JSONObject field = new JSONObject();
            field.put("id", header.getString("key"));
            field.put("label", header.getString("title"));
            field.put("type", StringUtils.defaultIfBlank(header.getString("type"), "text"));
            field.put("isCondition", header.getInteger("isCondition") == null ? 0 : header.getInteger("isCondition"));
            field.put("inputType", StringUtils.defaultIfBlank(header.getString("inputType"), "text"));
            field.put("config", IntegrationDefaultValueUtil.copyJson(header.get("config")));
            fields.add(field);
        }
        JSONObject result = new JSONObject();
        result.put("fieldList", fields);
        return result;
    }

    /** 白名单输入经原集成执行链处理，保留 limit 三态；权限拒绝异常按原类型向 API 传播。 */
    @Override
    public JSONObject searchDatasourceData(JSONObject params) throws PermissionDeniedException {
        String uuid = params.getString("dataSourceId");
        IntegrationVo integration = getIntegration(uuid);
        if (integration == null) {
            throw new IntegrationNotFoundException(uuid);
        }
        IntegrationAuthorityChecker.checkExecute(uuid, integrationMapper);
        IIntegrationHandler handler = getIntegrationHandler();
        JSONArray headers = getTheadList(integration, handler);
        System.out.println("headers = " + headers);
        JSONObject input = new JSONObject();
        for (String name : new String[]{"conditionList", "sortList", "currentPage", "pageSize", "limit", "isExpired"}) {
            if (params.containsKey(name)) {
                input.put(name, IntegrationDefaultValueUtil.copyJson(params.get(name)));
            }
        }
        BasePageVo page = new BasePageVo();
        if (params.getInteger("currentPage") != null && params.getInteger("currentPage") > 0) {
            page.setCurrentPage(params.getInteger("currentPage"));
        }
        page.setPageSize(params.getInteger("pageSize"));
        Integer limit = params.getInteger("limit");
        if (limit != null && limit < 0) {
            throw new ParamIrregularException("limit");
        }
        input.put("currentPage", page.getCurrentPage());
        input.put("pageSize", page.getPageSize());
        input.put("needPage", limit == null);
        // 输出表头不作为请求参数传递，自定义输入默认值由发送基类补齐。
        integration.setParamObj(input);
        IntegrationResultVo execution = handler.sendRequest(integration, FrameworkRequestFrom.DASHBOARD);
        // 调用或转换失败先向上抛出，只有成功结果才进入返回结构校验。
        if (execution == null) {
            throw new IntegrationRequestResultIsEmptyException();
        }
        if (StringUtils.isNotBlank(execution.getError())) {
            throw new DashboardIntegrationRequestFailedException(execution.getError());
        }
        if (execution.getStatusCode() > 0 && (execution.getStatusCode() < 200 || execution.getStatusCode() >= 300)) {
            throw new DashboardIntegrationRequestFailedException("HTTP " + execution.getStatusCode());
        }
        handler.validate(execution);
        JSONObject external = JSONObject.parseObject(execution.getTransformedResult());
        JSONArray theadList = external.getJSONArray("theadList");
        if (CollectionUtils.isEmpty(theadList)) {
            theadList = headers;
        }
//        JSONArray theadList = new JSONArray();
//        for (int i = 0; i < headers.size(); i++) {
//            JSONObject header = headers.getJSONObject(i);
//            JSONObject head = new JSONObject();
//            head.put("key", "field_" + header.getString("key"));
//            head.put("name", header.getString("key"));
//            head.put("title", header.getString("title"));
//            theadList.add(head);
//        }
        JSONArray tbodyList = new JSONArray();
        JSONArray externalRows = external.getJSONArray("tbodyList");
        System.out.println("externalRows = " + externalRows);
        for (int i = 0; i < externalRows.size(); i++) {
            JSONObject externalRow = externalRows.getJSONObject(i);
            JSONObject row = new JSONObject();
            for (int j = 0; j < headers.size(); j++) {
                String key = headers.getJSONObject(j).getString("key");
                System.out.println("key = " + key);
                // 只返回声明的字段，缺失项补 null；标量和嵌套 JSON 值均保持原类型。
//                row.put("field_" + key, externalRow.get(key));
                row.put(key, externalRow.get(key));
            }
            tbodyList.add(row);
        }
        JSONObject result = new JSONObject();
        result.put("theadList", theadList);
        result.put("tbodyList", externalRows);
        for (String name : new String[]{"currentPage", "pageSize", "pageCount", "rowNum"}) {
            result.put(name, external.get(name));
        }
        return result;
    }

    /** 详情和取数共用启用状态及规范检查，不能用其他类型集成绕过大屏契约。 */
    private IntegrationVo getIntegration(String uuid) {
        IntegrationVo integration = integrationMapper.getIntegrationByUuid(uuid);
        if (integration != null) {
            if (!Integer.valueOf(1).equals(integration.getIsActive())) {
                throw new IntegrationUnActiveException(uuid);
            }
            if (!HANDLER.equals(integration.getHandler())) {
                throw new DashboardIntegrationConfigInvalidException($.t("nmfddh.integrationdashboarddatasourcehandler.invalidhandler"));
            }
        }
        return integration;
    }

    /** 使用已注册的集成处理器，保留原 sendRequest 的认证、转换、默认值与审计链路。 */
    private IIntegrationHandler getIntegrationHandler() {
        IIntegrationHandler handler = IntegrationHandlerFactory.getHandler(HANDLER);
        if (handler == null) {
            throw new IntegrationHandlerNotFoundException(HANDLER);
        }
        return handler;
    }

    /** 仅合并当前输出规范的表头默认值并在副本上读取，禁止远程取数及修改持久化配置。 */
    private JSONArray getTheadList(IntegrationVo integration, IIntegrationHandler handler) {
        PatternVo thead = null;
        for (PatternVo pattern : IntegrationPatternUtil.merge(handler, integration.getConfig(), "output")) {
            if ("theadList".equals(pattern.getName())) {
                thead = pattern;
                break;
            }
        }
        if (thead == null || !IntegrationDefaultValueUtil.isEnabled(thead.getDefaultConfig())
                || !(thead.getDefaultConfig().get("value") instanceof List)
                || ((List<?>) thead.getDefaultConfig().get("value")).isEmpty()) {
            throw new DashboardIntegrationConfigInvalidException($.t("nmfddh.integrationdashboarddatasourcehandler.theadrequired"));
        }
        IntegrationDefaultValueUtil.validateDefault(thead);
        JSONArray headers = (JSONArray) IntegrationDefaultValueUtil.copyJson(thead.getDefaultConfig().get("value"));
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < headers.size(); i++) {
            JSONObject header = headers.getJSONObject(i);
            if (header == null || StringUtils.isBlank(header.getString("key")) || StringUtils.isBlank(header.getString("title"))) {
                throw new DashboardIntegrationConfigInvalidException($.t("nmfddh.integrationdashboarddatasourcehandler.invalidthead"));
            }
            if (!keys.add(header.getString("key"))) {
                throw new DashboardIntegrationConfigInvalidException($.t("nmfddh.integrationdashboarddatasourcehandler.duplicatekey", header.getString("key")));
            }
        }
        return headers;
    }
}
