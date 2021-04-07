/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.dao.mapper;

import com.beust.jcommander.Parameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: linbq
 * @since: 2021/4/1 11:44
 **/
public interface DependencyMapper {

    public List<Object> getCallerListByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callerField") String callerField,
            @Param("callee") Object callee,
            @Param("startNum") int startNum,
            @Param("pageSize") int pageSize);

    public Integer getCallerCountByCallee(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callee") Object callee);

    public int insertDependency(
            @Param("tableName") String tableName,
            @Param("calleeField") String calleeField,
            @Param("callerField") String callerField,
            @Param("callee") Object callee,
            @Param("caller") Object caller);

    public int deleteDependencyByCaller(
            @Param("tableName") String tableName,
            @Param("callerField") String callerField,
            @Param("caller") Object caller);
}
