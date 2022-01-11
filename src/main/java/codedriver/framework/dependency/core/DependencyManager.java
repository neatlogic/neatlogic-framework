/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.dto.DependencyInfoVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
     * @param clazz  引用关系处理器类
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    public static int clearAndInsert(Class<? extends IDependencyHandler> clazz, Object callee, Object caller) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        dependencyHandler.delete(caller);
        return dependencyHandler.insert(callee, caller);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param clazz  引用关系处理器类
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    public static int insert(Class<? extends IDependencyHandler> clazz, Object callee, Object caller) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        return dependencyHandler.insert(callee, caller);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param clazz  引用关系处理器类
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    public static int insert(Class<? extends IDependencyHandler> clazz, Object callee, Object caller, JSONObject config) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        return dependencyHandler.insert(callee, caller, config);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param clazz  引用关系处理器类
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param callerArray 调用者值（如：服务uuid）
     * @return
     */
    public static int insert(Class<? extends IDependencyHandler> clazz, Object callee, JSONArray callerArray) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        return dependencyHandler.insert(callee, callerArray);
    }
    /**
     * 删除引用关系
     *
     * @param clazz  引用关系处理器类
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    public static int delete(Class<? extends IDependencyHandler> clazz, Object caller) {
        IDependencyHandler dependencyHandler = DependencyHandlerFactory.getHandler(clazz.getSimpleName());
        if (caller instanceof List) {
            int sum = 0;
            for (Object c : (List) caller) {
                sum += dependencyHandler.delete(c);
            }
            return sum;
        } else {
            return dependencyHandler.delete(caller);
        }
    }

    /**
     * 查询引用列表
     *
     * @param calleeType 被调用者类型
     * @param callee     被调用者值（如：服务时间窗口uuid）
     * @return
     */
    public static List<DependencyInfoVo> getDependencyList(IFromType calleeType, Object callee, BasePageVo basePageVo) {
        List<DependencyInfoVo> resultList = new ArrayList<>();
        List<IDependencyHandler> dependencyHandlerList = DependencyHandlerFactory.getHandlerList(calleeType);
        int pageSize = basePageVo.getPageSize();
        int startNum = basePageVo.getStartNum();
        for (IDependencyHandler handler : dependencyHandlerList) {
            if (!handler.canBeLifted()) {
                continue;
            }
            if (pageSize == 0) {
                break;
            }
            int count = handler.getDependencyCount(callee);
            if (startNum > count) {
                startNum -= count;
                continue;
            }
            List<DependencyInfoVo> callerList = handler.getDependencyList(callee, startNum, pageSize);
            resultList.addAll(callerList);
            pageSize -= callerList.size();
            startNum = 0;
        }
        return resultList;
    }

    /**
     * 查询引用个数
     *
     * @param calleeType  被调用者类型
     * @param callee      被调用者值（如：服务时间窗口uuid）
     * @param canBeLifted 依赖关系能否解除
     * @return
     */
    public static int getDependencyCount(IFromType calleeType, Object callee, boolean canBeLifted) {
        int sum = 0;
        List<IDependencyHandler> dependencyHandlerList = DependencyHandlerFactory.getHandlerList(calleeType);
        if (CollectionUtils.isNotEmpty(dependencyHandlerList)) {
            for (IDependencyHandler handler : dependencyHandlerList) {
                if (handler.canBeLifted() == canBeLifted) {
                    sum += handler.getDependencyCount(callee);
                }
            }
        }
        return sum;
    }

    /**
     * 查询引用个数
     *
     * @param calleeType 被调用者类型
     * @param callee     被调用者值（如：服务时间窗口uuid）
     * @return
     */
    public static int getDependencyCount(IFromType calleeType, Object callee) {
        return getDependencyCount(calleeType, callee, true);
    }
}
