/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dependency.dao.mapper.DependencyMapper;
import codedriver.framework.dependency.dto.DependencyVo;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 依赖关系处理器基类
 *
 * @author: linbq
 * @since: 2021/4/1 11:43
 **/
public abstract class FixedTableDependencyHandlerBase implements IDependencyHandler {

    private static DependencyMapper dependencyMapper;

    @Resource
    public void setDependencyMapper(DependencyMapper _dependencyMapper) {
        dependencyMapper = _dependencyMapper;
    }
    /**
     * 插入一条引用关系数据
     *
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    @Override
    public int insert(Object callee, Object caller) {
        return insert(callee, caller, null);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param callee 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @param config 额外数据
     * @return
     */
    @Override
    public int insert(Object callee, Object caller, JSONObject config) {
        DependencyVo dependencyVo = new DependencyVo(callee.toString(), getHandler(), caller.toString(), config);
        return dependencyMapper.insertDependency(dependencyVo);
    }

    /**
     * 删除引用关系
     *
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    @Override
    public int delete(Object caller) {
        DependencyVo dependencyVo = new DependencyVo(getHandler(), caller.toString());
        return dependencyMapper.deleteDependency(dependencyVo);
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
        List<DependencyVo> callerList = dependencyMapper.getDependencyListByFrom(callee.toString(), getHandler(), startNum, pageSize);
        for (DependencyVo caller : callerList) {
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
        return dependencyMapper.getDependencyCountByFrom(callee.toString());
    }

    /**
     * 解析数据，拼装跳转url，返回引用下拉列表一个选项数据结构
     *
     * @param caller 调用者值
     * @return
     */
    protected abstract ValueTextVo parse(DependencyVo caller);
}
