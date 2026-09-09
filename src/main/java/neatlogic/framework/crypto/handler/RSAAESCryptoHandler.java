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
import neatlogic.framework.util.PasswordRSAUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class RSAAESCryptoHandler implements ICryptoHandler {
    /** 当前前后端约定的RSA包装AES密钥、AES-GCM加密密码的混合密文前缀。 */
    public static final String ENCRYPTED_PREFIX = "RSA.AES:";
    /** GCM使用128位认证标签，Java加密结果会将标签附加在密文末尾。 */
    private static final int GCM_TAG_BIT_LENGTH = 128;
    /** 当前混合协议使用点号分隔RSA密钥密文和AES密码载荷。 */
    private static final String HYBRID_SECTION_SEPARATOR = ".";
    /** AES密码正文使用GCM模式，同时提供机密性和完整性校验。 */
    private static final String AES_CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    /** GCM推荐使用96位随机IV，IV会随AES密文一起传输。 */
    private static final int GCM_IV_BYTE_LENGTH = 12;
    /** 每次密码加密均生成独立的256位AES密钥。 */
    private static final int AES_KEY_SIZE = 256;
    /** 密码加密所需随机数统一由安全随机数生成器提供。 */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public List<String> handlers() {
        // 工厂通过前缀选择处理器，新旧混合协议均由本处理器负责解密。
        return List.of(ENCRYPTED_PREFIX);
    }

    @Override
    public String encrypt(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            // 混合加密编排层只负责生成本次使用的密钥和IV、调用算法方法并拼装协议。
            SecretKey aesKey = generateAesKey();
            byte[] iv = generateGcmIv();
            byte[] encryptedPassword = encryptPasswordByAes(plainPassword, aesKey, iv);
            byte[] aesPayload = assembleAesPayload(iv, encryptedPassword);
            byte[] encryptedAesKey = PasswordRSAUtil.encryptAesKeyByRsa(aesKey);
            return ENCRYPTED_PREFIX
                    + Base64.getEncoder().encodeToString(encryptedAesKey)
                    + HYBRID_SECTION_SEPARATOR
                    + Base64.getEncoder().encodeToString(aesPayload);
        } catch (GeneralSecurityException e) {
            throw new PasswordEncryptException(e);
        }
    }

    @Override
    public String decrypt(String encryptedPassword) {
        if (encryptedPassword == null) {
            return null;
        }
        int separatorIndex = encryptedPassword.indexOf(HYBRID_SECTION_SEPARATOR, ENCRYPTED_PREFIX.length());
        if (separatorIndex <= ENCRYPTED_PREFIX.length()
                || separatorIndex != encryptedPassword.lastIndexOf(HYBRID_SECTION_SEPARATOR)
                || separatorIndex == encryptedPassword.length() - HYBRID_SECTION_SEPARATOR.length()) {
            throw new PasswordDecryptException("账号密码混合密文格式不正确");
        }
        String encryptedAesKeyBase64 = encryptedPassword.substring(ENCRYPTED_PREFIX.length(), separatorIndex);
        String aesPayloadBase64 = encryptedPassword.substring(separatorIndex + HYBRID_SECTION_SEPARATOR.length());

        byte[] aesPayload = Base64.getDecoder().decode(aesPayloadBase64);
        int minimumPayloadLength = GCM_IV_BYTE_LENGTH + GCM_TAG_BIT_LENGTH / Byte.SIZE;
        if (aesPayload.length < minimumPayloadLength) {
            throw new PasswordDecryptException("账号密码AES密文长度不正确");
        }
        byte[] iv = Arrays.copyOfRange(aesPayload, 0, GCM_IV_BYTE_LENGTH);
        byte[] encryptedPasswordAndTag = Arrays.copyOfRange(aesPayload, GCM_IV_BYTE_LENGTH, aesPayload.length);
        try {
            // RSA只负责解包AES密钥，AES工具只负责解密密码正文并校验GCM认证标签。
            SecretKey aesKey = PasswordRSAUtil.decryptAesKeyByRsa(encryptedAesKeyBase64);
            return decryptPasswordByAes(encryptedPasswordAndTag, aesKey, iv);
        } catch (Exception e) {
            throw new PasswordDecryptException(e);
        }
    }



    /**
     * 按“12字节IV + AES密文 + 16字节认证标签”组装密码载荷。
     */
    private static byte[] assembleAesPayload(byte[] iv, byte[] encryptedPasswordAndTag) {
        byte[] aesPayload = new byte[iv.length + encryptedPasswordAndTag.length];
        System.arraycopy(iv, 0, aesPayload, 0, iv.length);
        System.arraycopy(encryptedPasswordAndTag, 0, aesPayload, iv.length, encryptedPasswordAndTag.length);
        return aesPayload;
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

    /**
     * 使用AES-GCM解密“密文 + 认证标签”格式的密码正文。
     */
    private static String decryptPasswordByAes(byte[] encryptedPasswordAndTag, SecretKey aesKey, byte[] iv) throws GeneralSecurityException {
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
    private static byte[] encryptPasswordByAes(String plainPassword, SecretKey aesKey, byte[] iv) throws GeneralSecurityException {
        Cipher aesCipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION);
        aesCipher.init(
                Cipher.ENCRYPT_MODE,
                aesKey,
                new GCMParameterSpec(GCM_TAG_BIT_LENGTH, iv)
        );
        return aesCipher.doFinal(plainPassword.getBytes(StandardCharsets.UTF_8));
    }
}
