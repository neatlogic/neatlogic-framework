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

import neatlogic.framework.crypto.core.ICryptoHandler;
import neatlogic.framework.exception.util.PasswordDecryptException;
import neatlogic.framework.exception.util.PasswordEncryptException;
import neatlogic.framework.util.AESGCMUtil;
import neatlogic.framework.util.PasswordRSAUtil;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class RSAAESCryptoHandler implements ICryptoHandler {
    /** 前后端约定的AES-GCM与RSA混合密码密文前缀。 */
    public static final String ENCRYPTED_PREFIX = "AES:";
    /** GCM使用128位认证标签，Java加密结果会将标签附加在密文末尾。 */
    private static final int GCM_TAG_BIT_LENGTH = 128;
    /** 混合密文中RSA加密AES密钥部分的分隔标识。 */
    private static final String HYBRID_RSA_SECTION_PREFIX = ".RSA:";
    /** GCM推荐使用96位随机IV，IV会随AES密文一起传输。 */
    private static final int GCM_IV_BYTE_LENGTH = 12;
    /** 每次密码加密均生成独立的256位AES密钥。 */
    private static final int AES_KEY_SIZE = 256;
    /** 密码加密所需随机数统一由安全随机数生成器提供。 */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    @Override
    public List<String> handlers() {
        return List.of("AES:");
    }

    @Override
    public String encrypt(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            // 混合加密编排层只负责生成本次使用的密钥和IV，并调用AES、RSA专用方法。
            SecretKey aesKey = generateAesKey();
            byte[] iv = generateGcmIv();
            byte[] encryptedPassword = AESGCMUtil.encryptPasswordByAes(plainPassword, aesKey, iv);
            // AES载荷按“12字节IV + 密文 + 16字节认证标签”排列，供前端和后端统一解析。
            byte[] aesPayload = new byte[iv.length + encryptedPassword.length];
            System.arraycopy(iv, 0, aesPayload, 0, iv.length);
            System.arraycopy(encryptedPassword, 0, aesPayload, iv.length, encryptedPassword.length);

            byte[] encryptedAesKey = PasswordRSAUtil.encryptAesKeyByRsa(aesKey);
            return ENCRYPTED_PREFIX
                    + Base64.getEncoder().encodeToString(aesPayload)
                    + HYBRID_RSA_SECTION_PREFIX
                    + Base64.getEncoder().encodeToString(encryptedAesKey);
        } catch (GeneralSecurityException e) {
            throw new PasswordEncryptException(e);
        }
    }

    @Override
    public String decrypt(String encryptedPassword) {

        int separatorIndex = encryptedPassword.indexOf(HYBRID_RSA_SECTION_PREFIX, ENCRYPTED_PREFIX.length());
        if (separatorIndex <= ENCRYPTED_PREFIX.length()
                || separatorIndex != encryptedPassword.lastIndexOf(HYBRID_RSA_SECTION_PREFIX)) {
//            throw new GeneralSecurityException("账号密码混合密文格式不正确");
            throw new PasswordDecryptException("账号密码混合密文格式不正确");
        }
        String aesPayloadBase64 = encryptedPassword.substring(ENCRYPTED_PREFIX.length(), separatorIndex);
        String encryptedAesKeyBase64 = encryptedPassword.substring(separatorIndex + HYBRID_RSA_SECTION_PREFIX.length());
        if (encryptedAesKeyBase64.isEmpty()) {
            throw new PasswordDecryptException("账号密码混合密文缺少AES密钥");
//            throw new GeneralSecurityException("账号密码混合密文缺少AES密钥");
        }

        byte[] aesPayload = Base64.getDecoder().decode(aesPayloadBase64);
        int minimumPayloadLength = GCM_IV_BYTE_LENGTH + GCM_TAG_BIT_LENGTH / Byte.SIZE;
        if (aesPayload.length < minimumPayloadLength) {
            throw new PasswordDecryptException("账号密码AES密文长度不正确");
//            throw new GeneralSecurityException("账号密码AES密文长度不正确");
        }
        byte[] iv = Arrays.copyOfRange(aesPayload, 0, GCM_IV_BYTE_LENGTH);
        byte[] encryptedPasswordAndTag = Arrays.copyOfRange(aesPayload, GCM_IV_BYTE_LENGTH, aesPayload.length);
        try {
            // 混合解密编排层只负责解析协议，再将AES密钥和密码正文交给对应算法方法处理。
            SecretKey aesKey = PasswordRSAUtil.decryptAesKeyByRsa(encryptedAesKeyBase64);
            return AESGCMUtil.decryptPasswordByAes(encryptedPasswordAndTag, aesKey, iv);
        } catch (GeneralSecurityException e) {
            throw new PasswordEncryptException(e);
        }
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
