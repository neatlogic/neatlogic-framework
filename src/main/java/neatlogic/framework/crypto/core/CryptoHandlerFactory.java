/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.crypto.core;

import neatlogic.framework.crypto.handler.NoCryptoHandler;
import neatlogic.framework.reflection.ReflectionManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

public class CryptoHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(CryptoHandlerFactory.class);
    private static final ICryptoHandler noCryptoHandler = new NoCryptoHandler();
    private static final List<ICryptoHandler> list = new ArrayList<>();

    public static ICryptoHandler getCryptoHandlerByCiphertext(String ciphertext) {
        if (StringUtils.isBlank(ciphertext)) {
            return noCryptoHandler;
        }
        if (CollectionUtils.isEmpty(list)) {
            synchronized (CryptoHandlerFactory.class) {
                if (CollectionUtils.isEmpty(list)) {
                    init();
                }
            }
        }
        for (ICryptoHandler cryptoHandler : list) {
            if (CollectionUtils.isNotEmpty(cryptoHandler.handlers())) {
                for (String handler : cryptoHandler.handlers()) {
                    if (ciphertext.startsWith(handler)) {
                        return cryptoHandler;
                    }
                }
            }
        }
        return noCryptoHandler;
    }

    public static ICryptoHandler getCryptoHandlerByHandler(String handler) {
        if (CollectionUtils.isEmpty(list)) {
            synchronized (CryptoHandlerFactory.class) {
                if (CollectionUtils.isEmpty(list)) {
                    init();
                }
            }
        }
        for (ICryptoHandler cryptoHandler : list) {
            if (CollectionUtils.isNotEmpty(cryptoHandler.handlers()) && cryptoHandler.handlers().contains(handler)) {
                return cryptoHandler;
            }
        }
        return noCryptoHandler;
    }

    private static void init() {
        Reflections reflections = ReflectionManager.getInstance();
        Set<Class<? extends ICryptoHandler>> handlerClassSet = reflections.getSubTypesOf(ICryptoHandler.class);
        for (Class<? extends ICryptoHandler> clazz : handlerClassSet) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            try {
                list.add(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
