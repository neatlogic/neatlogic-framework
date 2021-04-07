/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.common.dto.ValueTextVo;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * 依赖关系处理器接口
 *
 * @author: linbq
 * @since: 2021/4/1 11:09
 **/
public interface IDependencyHandler {

    /**
     * 处理器唯一标识，全限定类名
     *
     * @return
     */
    public default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 被调用方名
     *
     * @return
     */
    public ICalleeType getCalleeType();

    /**
     * 依赖关系能否解除，比如服务引用时间窗口可以解除，但工单引用时间窗口不可以解除
     *
     * @return
     */
    public default boolean canBeLifted() {
        return true;
    }

    /**
     * 插入一条引用关系数据
     *
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    public int insert(Object callee, Object caller);

    /**
     * 删除引用关系
     *
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    public int delete(Object caller);

    /**
     * 查询引用列表数据
     *
     * @param callee   被调用者值（如：服务时间窗口uuid）
     * @param startNum 开始行号
     * @param pageSize 数据量
     * @return
     */
    public List<ValueTextVo> getCallerList(Object callee, int startNum, int pageSize);

    /**
     * 查询引用次数
     *
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @return
     */
    public int getCallerCount(Object callee);
}
