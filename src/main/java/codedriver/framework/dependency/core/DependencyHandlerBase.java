/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.dao.mapper.DependencyMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: linbq
 * @since: 2021/4/1 11:43
 **/
public abstract class DependencyHandlerBase implements IDependencyHandler {

    protected static DependencyMapper dependencyMapper;

    @Resource
    public void setDependencyMapper(DependencyMapper dependencyMapper) {
        this.dependencyMapper = dependencyMapper;
    }

    /**
     * 表名
     *
     * @return
     */
    protected abstract String getTableName();

    /**
     * 被调用者字段
     *
     * @return
     */
    protected abstract String getCalleeField();

    /**
     * 调用者字段
     *
     * @return
     */
    protected abstract String getCallerField();

    @Override
    public int insert(Object callee, Object caller) {
        return dependencyMapper.insertDependency(getTableName(), getCalleeField(), getCallerField(), callee, caller);
    }

    @Override
    public int delete(Object caller) {
        return dependencyMapper.deleteDependencyByCaller(getTableName(), getCallerField(), caller);
    }

    @Override
    public List<ValueTextVo> getCallerList(Object callee, int startNum, int pageSize) {
        List<ValueTextVo> resultList = new ArrayList<>();
        List<Object> callerList = dependencyMapper.getCallerListByCallee(getTableName(), getCalleeField(), getCallerField(), callee, startNum, pageSize);
        for (Object caller : callerList) {
            ValueTextVo valueTextVo = parse(caller);
            if (valueTextVo != null) {
                resultList.add(valueTextVo);
            }
        }
        return resultList;
    }

    @Override
    public int getCallerCount(Object callee) {
        System.out.println(this);
        System.out.println(getTableName());
        System.out.println(getCalleeField());
        System.out.println(callee);
        System.out.println(dependencyMapper);
        System.out.println(TenantContext.get().getTenantUuid());
        Integer count = dependencyMapper.getCallerCountByCallee(getTableName(), getCalleeField(), callee);
        System.out.println(count);
        return count;
    }

    /**
     * 解析数据，拼装跳转url
     *
     * @param caller
     * @return
     */
    protected abstract ValueTextVo parse(Object caller);
}
