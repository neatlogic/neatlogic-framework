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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dependency.dao.mapper.DependencyMapper;
import neatlogic.framework.dependency.dto.DependencyInfoVo;
import neatlogic.framework.dependency.dto.DependencyVo;
import neatlogic.framework.util.$;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 固定表结构依赖关系处理器基类
 *
 * @author: linbq
 * @since: 2021/4/1 11:43
 **/
public abstract class FixedTableDependencyHandlerBase implements IDependencyHandler {

    private String groupName;

    private static DependencyMapper dependencyMapper;

    @Resource
    public void setDependencyMapper(DependencyMapper _dependencyMapper) {
        dependencyMapper = _dependencyMapper;
    }

    @Override
    public String getGroupName() {
        return $.t(groupName);
    }

    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 插入一条引用关系数据
     *
     * @param from 被引用者（上游）值（如：服务时间窗口uuid）
     * @param to   引用者（下游）值（如：服务uuid）
     * @return
     */
    @Override
    public int insert(Object from, Object to) {
        return insert(from, to, null);
    }

    /**
     * 插入一条引用关系数据
     *
     * @param from   被引用者（上游）值（如：服务时间窗口uuid）
     * @param to     引用者（下游）值（如：服务uuid）
     * @param config 额外数据
     * @return
     */
    @Override
    public int insert(Object from, Object to, JSONObject config) {
        DependencyVo dependencyVo = new DependencyVo(from.toString(), getHandler(), to.toString(), config);
        return dependencyMapper.insertDependency(dependencyVo);
    }

    /**
     * 删除引用关系
     *
     * @param to 引用者（下游）值（如：服务uuid）
     * @return
     */
    @Override
    public int delete(Object to) {
        DependencyVo dependencyVo = new DependencyVo(getHandler(), to.toString());
        return dependencyMapper.deleteDependency(dependencyVo);
    }

    /**
     * 删除引用关系
     *
     * @param from 引用者（下游）值（如：服务uuid）
     * @return
     */
    @Override
    public int deleteByFrom(Object from) {
        DependencyVo dependencyVo = new DependencyVo(getHandler(), from.toString(),"");
        return dependencyMapper.deleteDependencyByFrom(dependencyVo);
    }

    /**
     * 查询引用列表数据
     *
     * @param from     被引用者（上游）值（如：服务时间窗口uuid）
     * @param startNum 开始行号
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public List<DependencyInfoVo> getDependencyList(Object from, int startNum, int pageSize) {
        List<DependencyInfoVo> resultList = new ArrayList<>();
        List<DependencyVo> toList = dependencyMapper.getDependencyListByFrom(from.toString(), getHandler(), startNum, pageSize);
        for (DependencyVo to : toList) {
            DependencyInfoVo valueTextVo = parse(to);
            if (valueTextVo != null) {
                resultList.add(valueTextVo);
            }
        }
        return resultList;
    }

    /**
     * 查询引用次数
     *
     * @param to 被引用者（上游）值（如：服务时间窗口uuid）
     * @return
     */
    @Override
    public int getDependencyCount(Object to) {
        return dependencyMapper.getDependencyCountByFrom(to, getHandler());
    }

    public List<Map<Object, Integer>> getBatchDependencyCount(Object fromList) {
        return dependencyMapper.getBatchDependencyCountByFrom((List<Object>) fromList, getHandler());
    }

    /**
     * 解析数据，拼装跳转url，返回引用下拉列表一个选项数据结构
     *
     * @param dependencyVo 引用关系数据
     * @return
     */
    protected abstract DependencyInfoVo parse(DependencyVo dependencyVo);
}
