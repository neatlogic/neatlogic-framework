/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.dependency.dao.mapper;

import neatlogic.framework.dependency.dto.DependencyVo;
import com.alibaba.fastjson.JSONArray;
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
