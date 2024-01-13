/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        int maxLength = 32000;
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
