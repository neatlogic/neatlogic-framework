/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.core;

import codedriver.framework.dependency.dao.mapper.DependencyMapper;
import codedriver.framework.dependency.dto.DependencyInfoVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 依赖关系处理器基类
 *
 * @author: linbq
 * @since: 2021/4/1 11:43
 **/
public abstract class CustomTableDependencyHandlerBase implements IDependencyHandler {

    protected static DependencyMapper dependencyMapper;

    @Resource
    public void setDependencyMapper(DependencyMapper _dependencyMapper) {
        dependencyMapper = _dependencyMapper;
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
    protected abstract String getFromField();

    /**
     * 调用者字段
     *
     * @return
     */
    protected abstract String getToField();

    /**
     * 调用者字段列表
     *
     * @return
     */
    protected abstract List<String> getToFieldList();

    /**
     * 插入一条引用关系数据
     *
     * @param from 被调用者值（如：服务时间窗口uuid）
     * @param caller 调用者值（如：服务uuid）
     * @return
     */
    @Override
    public int insert(Object from, Object caller) {
        return insert(from, caller, null);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param from 被调用者值（如：服务时间窗口uuid）
     * @param to 调用者值（如：服务uuid）
     * @param config 额外数据
     * @return
     */
    @Override
    public int insert(Object from, Object to, JSONObject config) {
        if(to instanceof JSONArray){
            return dependencyMapper.insertIgnoreDependencyForCallerFieldList(getTableName(), getFromField(), getToFieldList(), from, (JSONArray) to);
        } else {
            return dependencyMapper.insertIgnoreDependencyForCallerField(getTableName(), getFromField(), getToField(), from, to);
        }
    }

    /**
     * 删除引用关系
     *
     * @param to 调用者值（如：服务uuid）
     * @return
     */
    @Override
    public int delete(Object to) {
        return dependencyMapper.deleteDependencyByCaller(getTableName(), getToField(), to);
    }

    /**
     * 查询引用列表数据
     *
     * @param from   被调用者值（如：服务时间窗口uuid）
     * @param startNum 开始行号
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public List<DependencyInfoVo> getDependencyList(Object from, int startNum, int pageSize) {
        List<DependencyInfoVo> resultList = new ArrayList<>();
        List<Map<String, Object>> callerList = dependencyMapper.getCallerListByCallee(getTableName(), getFromField(), from, startNum, pageSize);
        for (Object caller : callerList) {
            DependencyInfoVo valueTextVo = parse(caller);
            if (valueTextVo != null) {
                resultList.add(valueTextVo);
            }
        }
        return resultList;
    }

    /**
     * 查询引用次数
     *
     * @param to 被调用者值（如：服务时间窗口uuid）
     * @return
     */
    @Override
    public int getDependencyCount(Object to) {
        return dependencyMapper.getCallerCountByCallee(getTableName(), getFromField(), to);
    }

    /**
     * 解析数据，拼装跳转url，返回引用下拉列表一个选项数据结构
     *
     * @param caller 调用者值
     * @return
     */
    protected abstract DependencyInfoVo parse(Object caller);
}
