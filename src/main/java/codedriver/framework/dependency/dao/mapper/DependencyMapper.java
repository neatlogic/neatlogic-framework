/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.dao.mapper;

import codedriver.framework.dependency.dto.DependencyVo;
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
