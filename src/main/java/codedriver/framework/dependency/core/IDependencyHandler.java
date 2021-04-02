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
 * @author: linbq
 * @since: 2021/4/1 11:09
 **/
public interface IDependencyHandler {

    public default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 被调用方名
     *
     * @return
     */
    public ICalleeType getCalleeType();

    public int insert(Object callee, Object caller);

    public int delete(Object caller);

    public List<ValueTextVo> getCallerList(Object callee, int startNum, int pageSize);

    public int getCallerCount(Object callee);
}
