/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.matrix.constvalue.MatrixType;
import codedriver.framework.matrix.dto.MatrixVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class MatrixPrivateDataSourceHandlerFactory extends ModuleInitializedListenerBase {

    private final static Logger logger = LoggerFactory.getLogger(MatrixPrivateDataSourceHandlerFactory.class);
    private final static Map<String, IMatrixPrivateDataSourceHandler> map = new HashMap<>();

    private final static List<MatrixVo> list = new ArrayList<>();

    public static IMatrixPrivateDataSourceHandler getHandler(String uuid) {
        return map.get(uuid);
    }

    /**
     * 根据uuid获取单个矩阵对象信息
     *
     * @param uuid 唯一标识
     * @return 单个矩阵对象信息
     */
    public static MatrixVo getMatrixVo(String uuid) {
        for (MatrixVo matrixVo : list) {
            if (matrixVo.getUuid().equals(uuid)) {
                return matrixVo;
            }
        }
        return null;
    }

    /**
     * 根据搜索条件统计符合条件矩阵总数
     *
     * @param searchVo 搜索条件
     * @return 矩阵总数
     */
    public static int getCount(MatrixVo searchVo) {
        return getNoPaginationList(searchVo).size();
    }

    /**
     * 根据搜索条件统计符合条件矩阵列表（分页）
     *
     * @param searchVo 搜索条件
     * @return 矩阵列表
     */
    public static List<MatrixVo> getList(MatrixVo searchVo) {
        List<MatrixVo> resultList = getNoPaginationList(searchVo);
        if (!searchVo.getNeedPage()) {
            return resultList;
        }
        int rowNum = resultList.size();
        searchVo.setRowNum(rowNum);
        if (searchVo.getCurrentPage() <= searchVo.getPageCount()) {
            int fromIndex = searchVo.getStartNum();
            int toIndex = fromIndex + searchVo.getPageSize();
            toIndex = Math.min(rowNum, toIndex);
            return resultList.subList(fromIndex, toIndex);
        }
        return new ArrayList<>();
    }

    /**
     * 根据搜索条件统计符合条件矩阵列表（不分页）
     *
     * @param searchVo 搜索条件
     * @return 矩阵列表
     */
    private static List<MatrixVo> getNoPaginationList(MatrixVo searchVo) {
        List<MatrixVo> resultList = new ArrayList<>();
        String keyword = searchVo.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.toLowerCase();
        }
        String type = searchVo.getType();
        for (MatrixVo matrixVo : list) {
            if (StringUtils.isNotBlank(keyword)) {
                if (!matrixVo.getName().toLowerCase().contains(keyword)) {
                    continue;
                }
            }
            if (StringUtils.isNotBlank(type)) {
                if (!type.equals(matrixVo.getType())) {
                    continue;
                }
            }
            resultList.add(matrixVo);
        }
        return resultList;
    }

    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IMatrixPrivateDataSourceHandler> myMap = context.getBeansOfType(IMatrixPrivateDataSourceHandler.class);
        for (Map.Entry<String, IMatrixPrivateDataSourceHandler> entry : myMap.entrySet()) {
            IMatrixPrivateDataSourceHandler matrixPrivateDataSourceHandler = entry.getValue();
            String uuid = matrixPrivateDataSourceHandler.getUuid();
            String name = matrixPrivateDataSourceHandler.getName();
            if (map.containsKey(uuid)) {
                logger.error("私有类型矩阵：" + name + "（" + uuid + "）" + "已存在，请检查代码，不要重复");
                System.exit(1);
            }
            map.put(uuid, matrixPrivateDataSourceHandler);

            MatrixVo matrixVo = new MatrixVo();
            matrixVo.setUuid(uuid);
            matrixVo.setName(name);
            matrixVo.setLabel(matrixPrivateDataSourceHandler.getLabel());
            matrixVo.setType(MatrixType.PRIVATE.getValue());
            list.add(matrixVo);
        }
    }

    @Override
    protected void myInit() {

    }
}
