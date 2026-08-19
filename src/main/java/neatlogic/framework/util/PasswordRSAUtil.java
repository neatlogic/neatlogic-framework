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

package neatlogic.framework.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.util.PasswordDecryptException;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * 账号密码传输用 RSA 密钥工具。私钥保存在data.home目录，避免服务重启后无法解密历史密码。
 */
public final class PasswordRSAUtil {

    /** 前后端约定的RSA密码密文前缀。 */
    public static final String ENCRYPTED_PREFIX = "{RSA}";
    public static final String ALGORITHM = "RSA-OAEP-256";
    /** JCE使用的RSA-OAEP算法名称，加密和解密必须保持一致。 */
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int KEY_SIZE = 2048;
    /** 2048位RSA使用OAEP-SHA256时，单次允许加密的最大明文字节数。 */
    private static final int MAX_PLAINTEXT_BYTE_LENGTH = 190;
    private static final String KEY_DIRECTORY = "cmdb-key";
    private static final String PRIVATE_KEY_FILE = "account-password-rsa-private.key";
    private static final KeyPair KEY_PAIR = loadOrGenerateKeyPair();
    private static final OAEPParameterSpec OAEP_PARAMETER_SPEC = new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
    );

    private PasswordRSAUtil() {
    }

    /**
     * 生成账号密码传输使用的 RSA 公钥和私钥。
     *
     * @return 2048 位 RSA 密钥对
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前运行环境不支持RSA算法", e);
        }
    }

    /**
     * 优先加载已持久化的私钥；首次运行时生成密钥对并原子写入data.home目录。
     */
    private static KeyPair loadOrGenerateKeyPair() {
        Path keyDirectory = Path.of(Config.DATA_HOME(), KEY_DIRECTORY);
        Path privateKeyPath = keyDirectory.resolve(PRIVATE_KEY_FILE);
        try {
            Files.createDirectories(keyDirectory);
            if (Files.exists(privateKeyPath)) {
                return loadKeyPair(privateKeyPath);
            }
            KeyPair generatedKeyPair = generateKeyPair();
            Path temporaryKeyPath = keyDirectory.resolve(PRIVATE_KEY_FILE + "." + UUID.randomUUID() + ".tmp");
            try {
                Files.writeString(
                        temporaryKeyPath,
                        Base64.getEncoder().encodeToString(generatedKeyPair.getPrivate().getEncoded()),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW,
                        StandardOpenOption.WRITE
                );
                setPrivateKeyFilePermission(temporaryKeyPath);
                try {
                    Files.move(temporaryKeyPath, privateKeyPath, StandardCopyOption.ATOMIC_MOVE);
                    return generatedKeyPair;
                } catch (FileAlreadyExistsException e) {
                    // 多实例共享data.home时使用已经由其他实例生成的私钥。
                    return loadKeyPair(privateKeyPath);
                } catch (AtomicMoveNotSupportedException e) {
                    try {
                        Files.move(temporaryKeyPath, privateKeyPath);
                        return generatedKeyPair;
                    } catch (FileAlreadyExistsException ignored) {
                        // 不支持原子移动的文件系统仍以最先创建成功的私钥为准。
                        return loadKeyPair(privateKeyPath);
                    }
                }
            } finally {
                Files.deleteIfExists(temporaryKeyPath);
            }
        } catch (Exception e) {
            throw new IllegalStateException("账号密码RSA密钥初始化失败", e);
        }
    }

    /**
     * 从PKCS#8私钥恢复公钥，保证只需持久化一个密钥文件。
     */
    private static KeyPair loadKeyPair(Path privateKeyPath) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(privateKeyPath, StandardCharsets.UTF_8).trim());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
        return new KeyPair(keyFactory.generatePublic(publicKeySpec), privateKey);
    }

    /**
     * POSIX系统将私钥权限收紧为仅当前用户可读写；Windows等文件系统保持平台默认权限。
     */
    private static void setPrivateKeyFilePermission(Path privateKeyPath) throws IOException {
        Set<PosixFilePermission> permissions = EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE
        );
        try {
            Files.setPosixFilePermissions(privateKeyPath, permissions);
        } catch (UnsupportedOperationException ignored) {
            // 当前文件系统不支持POSIX权限时无需额外处理。
        }
    }

    /**
     * 获取 X.509 SubjectPublicKeyInfo 格式的 Base64 公钥。
     */
    public static String getPublicKey() {
        return Base64.getEncoder().encodeToString(KEY_PAIR.getPublic().getEncoded());
    }

    /**
     * 判断密码是否为前端生成的RSA密文。
     */
    public static boolean isEncrypted(String password) {
        return password != null && password.startsWith(ENCRYPTED_PREFIX);
    }

    /**
     * 使用已加载的公钥加密明文密码，并增加用于标识RSA密文的前缀。
     *
     * @param plainPassword 待加密的明文密码
     * @return 带{RSA}前缀的Base64密文
     */
    public static String encrypt(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("待加密密码不能为空");
        }
        byte[] plainData = plainPassword.getBytes(StandardCharsets.UTF_8);
        if (plainData.length > MAX_PLAINTEXT_BYTE_LENGTH) {
            throw new IllegalArgumentException("密码内容过长，无法进行RSA加密");
        }
        try {
            // 使用与前端Web Crypto一致的OAEP-SHA256参数，保证前后端密文可以统一解密。
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, KEY_PAIR.getPublic(), OAEP_PARAMETER_SPEC);
            byte[] encryptedData = cipher.doFinal(plainData);
            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(encryptedData);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("密码RSA加密失败", e);
        }
    }

    /**
     * 使用已加载的私钥解密前端提交的 Base64 密文。
     */
    public static String decrypt(String encryptedPassword) {
        try {
            // Base64解码前移除用于区分历史密码格式的{RSA}前缀。
            String ciphertext = isEncrypted(encryptedPassword)
                    ? encryptedPassword.substring(ENCRYPTED_PREFIX.length())
                    : encryptedPassword;
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, KEY_PAIR.getPrivate(), OAEP_PARAMETER_SPEC);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new PasswordDecryptException(e);
        }
    }
}
