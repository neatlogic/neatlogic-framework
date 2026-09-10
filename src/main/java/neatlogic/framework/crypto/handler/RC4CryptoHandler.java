/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.crypto.handler;

import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.crypto.core.ICryptoHandler;

import java.util.List;

public class RC4CryptoHandler implements ICryptoHandler {
    @Override
    public List<String> handlers() {
        return List.of("RC4:", "{RC4}");
    }

    @Override
    public String encrypt(String plaintext) {
        return RC4Util.encrypt(plaintext);
    }

    @Override
    public String decrypt(String ciphertext) {
        return RC4Util.decrypt(ciphertext);
    }
}
