/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.changelog;

import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.util.JdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionHolder implements AutoCloseable {

    private Connection connection;

    TenantVo tenant = null;

    public ConnectionHolder() {
    }

    public ConnectionHolder(TenantVo tenant) {
        this.tenant = tenant;
    }

    public Connection get() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (tenant == null) {
                connection = JdbcUtil.getNeatlogicConnection();
            } else {
                connection = JdbcUtil.getNeatlogicTenantConnection(tenant, false);
            }
            //System.out.println("new:" + connection + ":" + connection.getCatalog());
        }
        //else {
            //System.out.println("old:" + connection + ":" + connection.getCatalog());
        //}
        return connection;
    }

    public void invalidate() throws SQLException {
        //System.out.println("invalidate:" + connection + ":" + connection.getCatalog());
        JdbcUtil.closeConnection(connection);
        connection = null;
    }

    @Override
    public void close() throws SQLException {
        //System.out.println("close:" + connection + ":" + connection.getCatalog());
        JdbcUtil.closeConnection(connection);
    }
}
