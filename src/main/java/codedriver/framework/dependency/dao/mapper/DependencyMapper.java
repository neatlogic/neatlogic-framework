/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.dao.mapper;

import com.alibaba.fastjson.JSONArray;
import com.beust.jcommander.Parameter;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: linbq
 * @since: 2021/4/1 11:44
 **/
public interface DependencyMapper {

    public List<Map<String, Object>> getCallerListByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") Object callee,
            @Param("startNum") int startNum,
            @Param("pageSize") int pageSize);

    public Integer getCallerCountByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") Object callee);

    public int insertIgnoreDependency(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callerField") String callerField,
            @Param("callee") Object callee,
            @Param("caller") Object caller);

    public int insertIgnoreDependency2(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callerFieldList") List<String> callerFieldList,
            @Param("callee") Object callee,
            @Param("callerArray") JSONArray callerArray);

    public int deleteDependencyByCaller(
            @Param("tableName") String tableName,
            @Param("callerField") String callerField,
            @Param("caller") Object caller);
}
