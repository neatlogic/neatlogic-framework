/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.dao.plugin;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.util.GzipUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompressHandler implements TypeHandler<String>, NeatLogicTypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, handleParameter(parameter));
    }

    @Override
    public String handleParameter(String parameter) {
        int maxLength = 1000;
        if (Config.ENABLE_GZIP() && StringUtils.isNotBlank(parameter) && parameter.length() > maxLength) {
            parameter = "GZIP:" + GzipUtil.compress(parameter);
        }
        return parameter;
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
