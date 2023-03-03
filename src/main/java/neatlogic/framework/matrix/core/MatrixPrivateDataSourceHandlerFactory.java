/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.matrix.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.matrix.constvalue.MatrixType;
import neatlogic.framework.matrix.dto.MatrixVo;
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
    protected void onInitialized(NeatLogicWebApplicationContext context) {
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
