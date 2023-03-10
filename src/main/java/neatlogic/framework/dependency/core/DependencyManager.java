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

package neatlogic.framework.dependency.core;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

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
     * @return
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
     * @return
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
     * @return
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
