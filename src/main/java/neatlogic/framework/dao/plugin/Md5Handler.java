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

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.util.DigestUtils;

import java.sql.*;
import java.util.Locale;

public class Md5Handler implements TypeHandler<Object>, NeatLogicTypeHandler<Object> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, handleParameter(parameter));
    }

    @Override
    public Object handleParameter(Object parameter) {
        if (parameter != null) {
            String parameterStr = parameter.toString().toLowerCase(Locale.ROOT);
            parameter = DigestUtils.md5DigestAsHex(parameterStr.getBytes());
        }
        return parameter;
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex);
    }

    public static void main(String[] args) {
        String parameter = "XX集团";
        String parameterStr = parameter.toString().toLowerCase(Locale.ROOT);
        parameter = DigestUtils.md5DigestAsHex(parameterStr.getBytes());
        System.out.println("parameter = " + parameter);
    }
}
