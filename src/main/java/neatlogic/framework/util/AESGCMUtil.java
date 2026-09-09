/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * AES-GCM加密工具
 */
public class AESGCMUtil {
    /** 前后端约定的AES-GCM与RSA混合密码密文前缀。 */
    public static final String ENCRYPTED_PREFIX = "AES:";
    /** AES密码正文使用GCM模式，同时提供机密性和完整性校验。 */
    private static final String AES_CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    /** GCM使用128位认证标签，Java加密结果会将标签附加在密文末尾。 */
    private static final int GCM_TAG_BIT_LENGTH = 128;
    /** GCM推荐使用96位随机IV，IV会随AES密文一起传输。 */
    private static final int GCM_IV_BYTE_LENGTH = 12;
    /** 每次密码加密均生成独立的256位AES密钥。 */
    private static final int AES_KEY_SIZE = 256;
    /** 密码加密所需随机数统一由安全随机数生成器提供。 */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 使用AES-GCM解密“密文 + 认证标签”格式的密码正文。
     */
    public static String decryptPasswordByAes(byte[] encryptedPasswordAndTag, SecretKey aesKey, byte[] iv) throws GeneralSecurityException {
        Cipher aesCipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION);
        aesCipher.init(
                Cipher.DECRYPT_MODE,
                aesKey,
                new GCMParameterSpec(GCM_TAG_BIT_LENGTH, iv)
        );
        return new String(aesCipher.doFinal(encryptedPasswordAndTag), StandardCharsets.UTF_8);
    }

    /**
     * 使用AES-GCM加密密码正文，返回“密文 + 认证标签”字节数据。
     */
    public static byte[] encryptPasswordByAes(String plainPassword, SecretKey aesKey, byte[] iv) throws GeneralSecurityException {
        Cipher aesCipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION);
        aesCipher.init(
                Cipher.ENCRYPT_MODE,
                aesKey,
                new GCMParameterSpec(GCM_TAG_BIT_LENGTH, iv)
        );
        return aesCipher.doFinal(plainPassword.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成单次AES-GCM密码加密使用的96位随机IV。
     */
    private static byte[] generateGcmIv() {
        byte[] iv = new byte[GCM_IV_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * 生成单次密码加密使用的256位AES密钥。
     */
    private static SecretKey generateAesKey() throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE, SECURE_RANDOM);
        return keyGenerator.generateKey();
    }
}
