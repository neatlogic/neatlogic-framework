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

package neatlogic.framework.dependency.dao.mapper;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.dependency.dto.DependencyVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author: linbq
 * @since: 2021/4/1 11:44
 **/
public interface DependencyMapper {

    List<Map<String, Object>> getCallerListByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") Object callee,
            @Param("startNum") int startNum,
            @Param("pageSize") int pageSize);

    Integer getCallerCountByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") Object callee);

    List<Map<Object, Integer>> getBatchCallerCountByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") List<Object> caller);

    int insertIgnoreDependencyForCallerField(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callerField") String callerField,
            @Param("callee") Object callee,
            @Param("caller") Object caller);

    int insertIgnoreDependencyForCallerFieldList(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callerFieldList") List<String> callerFieldList,
            @Param("callee") Object callee,
            @Param("callerArray") JSONArray callerArray);

    int deleteDependencyByCaller(
            @Param("tableName") String tableName,
            @Param("callerField") String callerField,
            @Param("caller") Object caller);

    int deleteDependencyByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") Object callee);

    int getDependencyCountByFrom(@Param("from") Object from, @Param("type") String type);

    List<DependencyVo> getDependencyListByFrom(@Param("from") String from, @Param("type") String type, @Param("startNum") int startNum, @Param("pageSize") int pageSize);

    int insertDependency(DependencyVo dependencyVo);

    int deleteDependency(DependencyVo dependencyVo);

    int deleteDependencyByFrom(DependencyVo dependencyVo);

    List<Map<Object, Integer>> getBatchDependencyCountByFrom(@Param("fromList") List<Object> fromList, @Param("type") String type);
}
