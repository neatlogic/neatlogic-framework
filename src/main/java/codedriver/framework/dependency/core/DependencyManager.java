/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.dependency.dto.DependencyInfoVo;
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
                        if (Objects.isNull(returnMap.get(caller))) {
                            returnMap.put(caller,callerCount);
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
