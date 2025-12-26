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
package neatlogic.framework.exception.login;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class LoginExpiredException extends ApiRuntimeException {

    @Serial
    private static final long serialVersionUID = -2759790625283616257L;

    public LoginExpiredException() {
        super("nff.jsonwebtokenvalidfilter.dofilterinternal.unexpired");
    }

    public LoginExpiredException(String tmp) {
        super("nff.jsonwebtokenvalidfilter.dofilterinternal.unexpired");
    }
}
