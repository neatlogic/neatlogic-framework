/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.dependency.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 依赖关系管理类，基本操作：保存依赖关系数据，删除依赖关系数据，查询依赖数量，查询引用列表
 *
 * @author: linbq
 * @since: 2021/4/1 12:03
 **/
public class DependencyManager {

    /**
     * 先清空再插入一条引用关系数据
     *
     * @param clazz 引用关系处理器类
     * @param from  被引用者（上游）值（如：服务时间窗口uuid）
     * @param to    引用者（下游）值（如：服务uuid）
     * @return
     */
    public static int clearAndInsert(Class<? extends IDependencyHandler> clazz, Object from, Object to) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        dependencyHandler.delete(to);
        return dependencyHandler.insert(from, to);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param clazz 引用关系处理器类
     * @param from  被引用者（上游）值（如：服务时间窗口uuid）
     * @param to    引用者（下游）值（如：服务uuid）
     * @return
     */
    public static int insert(Class<? extends IDependencyHandler> clazz, Object from, Object to) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        return dependencyHandler.insert(from, to);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param clazz 引用关系处理器类
     * @param from  被引用者（上游）值（如：服务时间窗口uuid）
     * @param to    引用者（下游）值（如：服务uuid）
     */
    public static int insert(Class<? extends IDependencyHandler> clazz, Object from, Object to, JSONObject config) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        return dependencyHandler.insert(from, to, config);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param clazz   引用关系处理器类
     * @param from    被引用者（上游）值（如：服务时间窗口uuid）
     * @param toArray 引用者（下游）值（如：服务uuid）
     */
    public static int insert(Class<? extends IDependencyHandler> clazz, Object from, JSONArray toArray) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        return dependencyHandler.insert(from, toArray);
    }

    /**
     * 删除引用关系
     *
     * @param clazz 引用关系处理器类
     * @param to    引用者（下游）值（如：服务uuid）
     */
    public static int delete(Class<? extends IDependencyHandler> clazz, Object to) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        if (to instanceof List) {
            int sum = 0;
            for (Object c : (List) to) {
                sum += dependencyHandler.delete(c);
            }
            return sum;
        } else {
            return dependencyHandler.delete(to);
        }
    }

    /**
     * 删除引用关系
     *
     * @param fromType 被引用者（上游）类型
     * @param from     被引用者（上游）值
     * @return 删除个数
     */
    public static int deleteByFrom(IFromType fromType, Object from) {
        int sum = 0;
        List<IDependencyHandler> dependencyHandlerList = DependencyHandlerFactory.getHandlerList(fromType);
        if (CollectionUtils.isNotEmpty(dependencyHandlerList)) {
            for (IDependencyHandler handler : dependencyHandlerList) {
                sum += handler.deleteByFrom(from);
            }
        }
        return sum;
    }

    /**
     * 查询引用列表
     *
     * @param fromType 被引用者（上游）类型
     * @param from     被引用者（上游）值（如：服务时间窗口uuid）
     * @return
     */
    public static List<DependencyInfoVo> getDependencyList(IFromType fromType, Object from, BasePageVo basePageVo) {
        List<DependencyInfoVo> resultList = new ArrayList<>();
        List<IDependencyHandler> dependencyHandlerList = DependencyHandlerFactory.getHandlerList(fromType);
        int pageSize = basePageVo.getPageSize();
        int startNum = basePageVo.getStartNum();
        for (IDependencyHandler handler : dependencyHandlerList) {
            if (!handler.canBeLifted()) {
                continue;
            }
            if (pageSize == 0) {
                break;
            }
            int count = handler.getDependencyCount(from);
            if (startNum > count) {
                startNum -= count;
                continue;
            }
            List<DependencyInfoVo> toList = handler.getDependencyList(from, startNum, pageSize);
            resultList.addAll(toList);
            pageSize -= toList.size();
            startNum = 0;
        }
        return resultList;
    }

    /**
     * 查询引用列表
     *
     * @param clazz 处理器
     * @param from  被引用者（上游）值（如：服务时间窗口uuid）
     * @return
     */
    public static List<DependencyInfoVo> getDependencyList(Class<? extends IDependencyHandler> clazz, Object from) {
        List<DependencyInfoVo> resultList = new ArrayList<>();
        IDependencyHandler handler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        int pageSize = 100;
        int count = handler.getDependencyCount(from);
        for (int startNum = 0; startNum < count; startNum += pageSize) {
            List<DependencyInfoVo> toList = handler.getDependencyList(from, startNum, pageSize);
            resultList.addAll(toList);
        }
        return resultList;
    }

    /**
     * 查询引用个数
     *
     * @param fromType    被引用者（上游）类型
     * @param from        被引用者（上游）值（如：服务时间窗口uuid）
     * @param canBeLifted 依赖关系能否解除
     * @return
     */
    public static int getDependencyCount(IFromType fromType, Object from, boolean canBeLifted) {
        int sum = 0;
        List<IDependencyHandler> dependencyHandlerList = DependencyHandlerFactory.getHandlerList(fromType);
        if (CollectionUtils.isNotEmpty(dependencyHandlerList)) {
            for (IDependencyHandler handler : dependencyHandlerList) {
                if (handler.canBeLifted() == canBeLifted) {
                    sum += handler.getDependencyCount(from);
                }
            }
        }
        return sum;
    }

    /**
     * 查询引用个数
     *
     * @param fromType 被引用者（上游）类型
     * @param from     被引用者（上游）值（如：服务时间窗口uuid）
     * @return
     */
    public static int getDependencyCount(IFromType fromType, Object from) {
        return getDependencyCount(fromType, from, true);
    }

    /**
     * 批量查询引用个数
     *
     * @param fromType
     * @param fromList
     * @param canBeLifted
     * @return
     */
    public static Map<Object, Integer> getBatchDependencyCount(IFromType fromType, Object fromList, boolean canBeLifted) {
        Map<Object, Integer> returnMap = new HashMap<>();
        List<IDependencyHandler> dependencyHandlerList = DependencyHandlerFactory.getHandlerList(fromType);
        if (CollectionUtils.isNotEmpty(dependencyHandlerList)) {
            for (IDependencyHandler handler : dependencyHandlerList) {
                if (handler.canBeLifted() == canBeLifted) {
                    for (Map<Object, Integer> map : handler.getBatchDependencyCount(fromList)) {
                        Object caller = map.get("caller");
                        Integer callerCount = Integer.parseInt(String.valueOf(map.get("callerCount")));
                        if ((returnMap.get(caller) == null)) {
                            returnMap.put(caller, callerCount);
                        } else {
                            returnMap.put(caller, returnMap.get(caller) + callerCount);
                        }
                    }
                }
            }
        }
        return returnMap;
    }

    /**
     * 批量查询引用个数
     *
     * @param fromType
     * @param fromList
     * @return
     */
    public static Map<Object, Integer> getBatchDependencyCount(IFromType fromType, Object fromList) {
        return getBatchDependencyCount(fromType, fromList, true);
    }

    /**
     * 批量查询引用列表
     *
     * @param fromType 被引用者（上游）类型
     * @param fromList 被引用者（上游）值（如：服务时间窗口uuid）
     * @return
     */
    public static Map<Object, List<DependencyInfoVo>> getBatchDependencyList(IFromType fromType, Object fromList, BasePageVo basePageVo) {
        Map<Object, List<DependencyInfoVo>> returnMap = new HashMap<>();
        for (Object from : (List<Object>) fromList) {
            returnMap.put(from, getDependencyList(fromType, from, basePageVo));
        }
        return returnMap;
    }
}
