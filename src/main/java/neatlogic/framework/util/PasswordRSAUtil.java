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

import neatlogic.framework.exception.util.PasswordDecryptException;
import neatlogic.framework.exception.util.PasswordEncryptException;
import neatlogic.framework.exception.util.PasswordTooLongException;
import neatlogic.framework.exception.util.RSAPrivateKeyInitializationFailedException;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * 账号密码传输用 RSA 密钥工具。私钥保存在data.home目录，避免服务重启后无法解密历史密码。
 */
public final class PasswordRSAUtil {

    /** 8192位PKCS#8私钥，与前端接口返回的公钥配套使用。 */
    private static final String PRIVATE_KEY = "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";

    /** 8192位X.509 SPKI公钥，用于核对固定密钥对配置。 */
    private static final String PUBLIC_KEY = "MIIEIjANBgkqhkiG9w0BAQEFAAOCBA8AMIIECgKCBAEAvolwxU2yRoX7WdZnGZWYGNnt3Koz5m3TbnIN6V+hblnCW9pfams1wMFvbsSDwf5SD/fkuM43Z/81jmsy9u+IegI0kTNjyECTiURuovM7R0U5o5we5TokH6QORHWD+MweLomeC3OfERMM5PV5obzdD8FGFxlb21s3MI6euqVK33r8eLUoDczazGvnAMCv7rSdrQZYDY7sB68N9vOKLPkH/E7fHVWCLpER4Zjsb1lXO4XMxfpd8Lv7CfzUF6BN73eTZOZpDrh5eEiyFmq5OVneDnDpCqVIufGlYUj6xfyDzKeoqsFhX4hSmEFLN/aONzQKlDwytfLrjiSSOpN67O93BRKlM5CzsxA+o3OZkx9wfwNN7ARDdVPLc37Ney+m9tpWm5iDuqEBs6db/FcJrQ25va6q6Q6U8uGCXsaUY4Z6sW7YZsw4edOxJ/FVJGyW28WIYw3TzicIDvNPFhO2aFaQc0orIKXoJm57zWNwCOLnnnih3LwhLaHdw9B7TvRgqvVPrqs5f3fpWJm5uUJ1dlLzCMTWL/NJjv47OI5LqGD7IvbiQZW0o4v/yMR2K4QPw1mzbK1w62zYj5AqJstUOI+Q4+r7x67f5Ci0GrUFyXnYeL3P7iMkeSK8mwVBRZyVEi988nIzDoAggDR4RFdhF4AT2bc2qJqLfNuqMM/1CxT295Djbp5CFn95a3lOIfqkgCLRqS0Ih/zy2xnpMMHJO3QRNpMEtwsRVkhCcwAfetM1l3o0OmxfSiW5LHAjwpKzq9C0XxubAMRdgBeU1NxSK6w9H7AKzI8ts5WcGlyNyeT6l+RUhJlavulqHiDbZ62/3N32KZB3yaJvLwtUGD6o9vdHrNetgcwG7So6oJHZDIvx45a4jWtmmfQ6ty78xIc8m3HXQUWiRWApUcYi+FR+lHaI/7M19sVKU7VOwsr/zzxMpr0TbXt4bS84i4GYvicVf+fbJcbZaEc4hLFJaqu+DP4zfmCW+Av6l3WQnWVV4APs5JeCpqLodynAFNet9vB4O9DGIJqweEjm01yk4ctM00zNW4sItAjpNLDNWlZcx7oq/gmxOoyC6Chu62k3iiESSNQ9ebI6TPwFm1y7IerKLykLWBNNgcMhn6CNEByg7jvm7LhYt0K0ZCQilHyJ0Md4sTVFbMlHtsHo+iuBmW2ByRXXBPHJdgrXtXTs7H1GoqI6HkyJlURTiKb8Nl6svsHgReX80eIivYtMv7Qoh/iRhE76CNsHUl+qDtOcjTuArFrDQkRtwWvVo7NVxybPSjvRwpm1yNxWzsAxLtX1eRhSJ+2+q59QyH6BYNkxNI4bH9Lb+tyDvEgs1rWzvO4AiVQxBhvCHux4b1ozeXJZPw59IFPCLQIDAQAB";

    /** 前后端约定的RSA密码密文前缀。 */
    public static final String ENCRYPTED_PREFIX = "RSA:";
    public static final String ALGORITHM = "RSA-OAEP-256";
    /** JCE使用的RSA-OAEP算法名称，加密和解密必须保持一致。 */
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    /** RSA密钥长度统一为8192位。 */
    private static final int KEY_SIZE = 8192;
    /** 8192位RSA使用OAEP-SHA256时，单次允许加密的最大明文字节数为1024-2*32-2=958。 */
    private static final int MAX_PLAINTEXT_BYTE_LENGTH = 958;
//    private static final String KEY_DIRECTORY = "rsa-key";
//    private static final String PRIVATE_KEY_FILE = "password-rsa-private.key";
//    private static final KeyPair KEY_PAIR = loadOrGenerateKeyPair();
    private static final KeyPair KEY_PAIR = loadKeyPairByPrivateKey(PRIVATE_KEY);
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
     * @return 8192 位 RSA 密钥对
     */
//    private static KeyPair generateKeyPair() {
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(KEY_SIZE);
//            return keyPairGenerator.generateKeyPair();
//        } catch (NoSuchAlgorithmException e) {
//            throw new IllegalStateException("当前运行环境不支持RSA算法", e);
//        }
//    }

    /**
     * 优先加载已持久化的私钥；首次运行时生成密钥对并原子写入data.home目录。
     */
//    private static KeyPair loadOrGenerateKeyPair() {
//        Path keyDirectory = Path.of(Config.DATA_HOME(), KEY_DIRECTORY);
//        Path privateKeyPath = keyDirectory.resolve(PRIVATE_KEY_FILE);
//        try {
//            Files.createDirectories(keyDirectory);
//            if (Files.exists(privateKeyPath)) {
//                return loadKeyPair(privateKeyPath);
//            }
//            KeyPair generatedKeyPair = generateKeyPair();
//            Path temporaryKeyPath = keyDirectory.resolve(PRIVATE_KEY_FILE + "." + UUID.randomUUID() + ".tmp");
//            try {
//                Files.writeString(
//                        temporaryKeyPath,
//                        Base64.getEncoder().encodeToString(generatedKeyPair.getPrivate().getEncoded()),
//                        StandardCharsets.UTF_8,
//                        StandardOpenOption.CREATE_NEW,
//                        StandardOpenOption.WRITE
//                );
//                setPrivateKeyFilePermission(temporaryKeyPath);
//                try {
//                    Files.move(temporaryKeyPath, privateKeyPath, StandardCopyOption.ATOMIC_MOVE);
//                    return generatedKeyPair;
//                } catch (FileAlreadyExistsException e) {
//                    // 多实例共享data.home时使用已经由其他实例生成的私钥。
//                    return loadKeyPair(privateKeyPath);
//                } catch (AtomicMoveNotSupportedException e) {
//                    try {
//                        Files.move(temporaryKeyPath, privateKeyPath);
//                        return generatedKeyPair;
//                    } catch (FileAlreadyExistsException ignored) {
//                        // 不支持原子移动的文件系统仍以最先创建成功的私钥为准。
//                        return loadKeyPair(privateKeyPath);
//                    }
//                }
//            } finally {
//                Files.deleteIfExists(temporaryKeyPath);
//            }
//        } catch (Exception e) {
//            throw new IllegalStateException("账号密码RSA密钥初始化失败", e);
//        }
//    }

    /**
     * 从PKCS#8私钥恢复公钥，保证只需持久化一个密钥文件。
     */
//    private static KeyPair loadKeyPair(Path privateKeyPath) throws Exception {
//        System.out.println("Files.readString(privateKeyPath, StandardCharsets.UTF_8).trim() = " + Files.readString(privateKeyPath, StandardCharsets.UTF_8).trim());
//        byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(privateKeyPath, StandardCharsets.UTF_8).trim());
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
//        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
//        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
//        return new KeyPair(keyFactory.generatePublic(publicKeySpec), privateKey);
//    }

    /**
     * 从PKCS#8私钥恢复公钥，保证只需持久化一个密钥文件。
     */
    private static KeyPair loadKeyPairByPrivateKey(String privateKeyStr) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
            return new KeyPair(keyFactory.generatePublic(publicKeySpec), privateKey);
        } catch (Exception e) {
            throw new RSAPrivateKeyInitializationFailedException(e);
        }
    }

    /**
     * POSIX系统将私钥权限收紧为仅当前用户可读写；Windows等文件系统保持平台默认权限。
     */
//    private static void setPrivateKeyFilePermission(Path privateKeyPath) throws IOException {
//        Set<PosixFilePermission> permissions = EnumSet.of(
//                PosixFilePermission.OWNER_READ,
//                PosixFilePermission.OWNER_WRITE
//        );
//        try {
//            Files.setPosixFilePermissions(privateKeyPath, permissions);
//        } catch (UnsupportedOperationException ignored) {
//            // 当前文件系统不支持POSIX权限时无需额外处理。
//        }
//    }

    /**
     * 获取 X.509 SubjectPublicKeyInfo 格式的 Base64 公钥。
     */
    public static String getPublicKey() {
        return Base64.getEncoder().encodeToString(KEY_PAIR.getPublic().getEncoded());
    }

    /**
     * RSA密钥长度统一为8192位。
     * @return
     */
    public static int getKeySize() {
        PrivateKey privateKey = KEY_PAIR.getPrivate();
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
        return rsaPrivateKey.getModulus().bitLength();
    }

    /**
     * 8192位RSA使用OAEP-SHA256时，单次允许加密的最大明文字节数为8192/8-2*32-2=958。
     * @return
     */
    public static int getMaxPlaintextByteLength() {
        return getKeySize() / 8 - 2 * 32 - 2;
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
     * @return 带RSA:前缀的Base64密文
     */
    public static String encrypt(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        byte[] plainData = plainPassword.getBytes(StandardCharsets.UTF_8);
        if (plainData.length > MAX_PLAINTEXT_BYTE_LENGTH) {
            throw new PasswordTooLongException();
        }
        try {
            // 使用与前端Web Crypto一致的OAEP-SHA256参数，保证前后端密文可以统一解密。
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, KEY_PAIR.getPublic(), OAEP_PARAMETER_SPEC);
            byte[] encryptedData = cipher.doFinal(plainData);
            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(encryptedData);
        } catch (GeneralSecurityException e) {
            throw new PasswordEncryptException(e);
        }
    }

    /**
     * 使用已加载的私钥解密前端提交的 Base64 密文。
     */
    public static String decrypt(String encryptedPassword) {
        try {
            // Base64解码前移除用于区分历史密码格式的RSA:前缀。
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
