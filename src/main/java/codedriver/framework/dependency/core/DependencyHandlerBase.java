/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.dao.mapper.DependencyMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 依赖关系处理器基类
 *
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

    /**
     * 插入一条引用关系数据
     *
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    @Override
    public int insert(Object callee, Object caller) {
        return dependencyMapper.insertIgnoreDependency(getTableName(), getCalleeField(), getCallerField(), callee, caller);
    }

    /**
     * 删除引用关系
     *
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    @Override
    public int delete(Object caller) {
        return dependencyMapper.deleteDependencyByCaller(getTableName(), getCallerField(), caller);
    }

    /**
     * 查询引用列表数据
     *
     * @param callee   被调用者值（如：服务时间窗口uuid）
     * @param startNum 开始行号
     * @param pageSize 每页条数
     * @return
     */
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

    /**
     * 查询引用次数
     *
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @return
     */
    @Override
    public int getCallerCount(Object callee) {
        return dependencyMapper.getCallerCountByCallee(getTableName(), getCalleeField(), callee);
    }

    /**
     * 解析数据，拼装跳转url，返回引用下拉列表一个选项数据结构
     *
     * @param caller 调用者值
     * @return
     */
    protected abstract ValueTextVo parse(Object caller);
}
