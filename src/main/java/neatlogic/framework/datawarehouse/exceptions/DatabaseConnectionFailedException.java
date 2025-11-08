/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
