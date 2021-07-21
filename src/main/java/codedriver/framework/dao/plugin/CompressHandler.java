/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.plugin;

import codedriver.framework.common.config.Config;
import codedriver.framework.util.GzipUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompressHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        if (Config.ENABLE_GZIP() && StringUtils.isNotBlank(parameter) && parameter.length() > 500) {//大于500个字符才开始压缩
            parameter = "GZIP:" + GzipUtil.compress(parameter);
        }
        ps.setString(i, parameter);
    }


    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        String v = rs.getString(columnName);
        if (StringUtils.isNotBlank(v) && v.startsWith("GZIP:")) {
            v = GzipUtil.uncompress(v.substring(5));
        }
        return v;
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        String v = rs.getString(columnIndex);
        if (StringUtils.isNotBlank(v) && v.startsWith("GZIP:")) {
            v = GzipUtil.uncompress(v.substring(5));
        }
        return v;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String v = cs.getString(columnIndex);
        if (StringUtils.isNotBlank(v) && v.startsWith("GZIP:")) {
            v = GzipUtil.uncompress(v.substring(5));
        }
        return v;
    }

}
