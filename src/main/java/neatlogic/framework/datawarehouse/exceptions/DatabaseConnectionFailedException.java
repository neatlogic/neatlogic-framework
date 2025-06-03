/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.datawarehouse.exceptions;

import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.util.$;

public class DatabaseConnectionFailedException extends ApiRuntimeException {

    public enum Type {
        CONFIG_IS_EMPTY, FILE_ID_LIST_IS_EMPTY
    }
    public DatabaseConnectionFailedException(Type type, String name) {
        super(getMessage(type, name));
    }

    private static String getMessage(Type type, String name) {
        if (type == Type.FILE_ID_LIST_IS_EMPTY) {
            return $.t("nfde.databaseconnectionfailedexception.fileidlistisempty", name);
        } else {
            return $.t("{0}nfde.databaseconnectionfailedexception.configisempty", name);
        }
    }
}
