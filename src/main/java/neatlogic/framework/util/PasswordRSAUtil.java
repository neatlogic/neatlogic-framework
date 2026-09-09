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
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * 账号密码传输用RSA加密工具。
 */
public final class PasswordRSAUtil {

    /** 2048位PKCS#8私钥，与前端接口返回的公钥配套使用。 */
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgsqfWg+kWqAW21X2krv/7kJ/Fyua49bgRi3Y02KlcP5ZYTcNvqg8Xs4LgoWFtfhf2kKLTHhF5JN5h8DMnq5p8z6xwa9JRzDe7v0r9sOPKYWFWkD/LWPMr7zcHL4L1qveI2xjb2yxMWm7buF0Rk+yO0am1THt3wxiodV4BK/LWm5ZZaZzwbrVVQwVel52ghY8l2PHgB+qIlt2gNSdGtjKPcxP5iMUX3STaDGhXDqBBPH7P97y01LHOsRUmgAMFRjyJQUJiCL5RMEmO3MdgcIXHwkfyMYByxGd7lSLc5yQjG1hdT1Dr2BtXQ80+1IhIUxCG2EiznQewYtVr40rGAEtnAgMBAAECggEAQt9l4eqd7ow4aIf6U7RxmTXrjytrDTBQC5kONVquS9G4VoHx4P+TbUkKH0F5Ik/1V+mDoLhkDBZQJtCG8SzDysm/WD8+VETYMpyd7+mTOa6Bi7zWl2AqPa+8JhTa+jHN9dk7RI62JgYCGRRlQoHc0OgmJ+iufr2k/sFsv68SzMRkNuggCWjXYuXTq2i7bUyuKLp4sm/47Rw5dkH36HGsW4fhltA4+T6+qEyyETNPaEoT1lQ+2jqjMBHyWOjZNBmlW85gUeSB/XZPnOWqEvPDzBOmgErmZIntJPHbFCoReb02/Kdrt4IRNI9sMirgSTQsn2UD+tBXBO+Hs3Wvtk2oJQKBgQC8/rHda23xs0vWMXgbpyd82CGXpQ/1rqqUrhAyMsdlPkpXbq4aEz7C3IF3NTfb5VBagaukJDI2fX/iYY+rnB7DtX6uzfICQn41cCnNK8htrFSxpJlFL0t0p2NgJHxBe7k9mDYxBS9fTj9Hs9F3DBFd8IK/l7IEbpw0GS4+HLaYCwKBgQDZq7QS4NIRKjnrMMe3C+Qlv8OR7DntgpK+fdarmpu+ygQTT8fMSPO/KuHRR3xR7dSDokGXKgwLuvONOKgjNihMZAqgwUOeUNdX4E2GlCPdjdpdW+Q/kWXxgHqLk+gJmx9SCR2IbE9DQ6CWhKINElTDgsokwvwov+yEuyxhrCSHlQKBgBLdocemh60O5s0U1xZ7kxeFQ6UtlvBBZUm+LmO0ae8TTrx3ke2MakFtXYcWyuKqe2DtfMK/0jtaP7/LWVoaFYAXx/OPH09Wb97JuYJ/klxQTYwGED61v+R/KQ5Z1gV7Yjxhy1cNW3M6DlsL+ibWD42/Cm4xqFWE7RbORK1ylE+NAoGAFMPYjeR8pb6Nf+5LXx73SNKeaZFLhWjrbti6XeyF5xGGigEWYlqjRh1lJX3YUkiJ+XTFJRKRy5yuF07MW2+TMJZqnSNSvAuiP3PacXg4Y65gon9dquLIAt3q0t3tSN1Pg5fzBUyv0w7khvdoLi8Nfwk/F3qya4DDo3XnqfmuEnECgYEAlleqGGVaAVc9oZj+LaNTZy0pwfIJrZOOeamA36j1R4A/kqKjJYttxIOF99IGDr+dRWhB0ml4iQi1BT9armk+M2lu01KjF6R4aEj0FrWcn/Nx/9SIv6p6IyokvC1SuC40cRhNgz3l1BcVWc8AZQUi65m8Mdnqp7pRVBwO0VAhn0k=";
    /** 2048位X.509 SPKI公钥，用于核对固定密钥对配置。 */
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoLKn1oPpFqgFttV9pK7/+5CfxcrmuPW4EYt2NNipXD+WWE3Db6oPF7OC4KFhbX4X9pCi0x4ReSTeYfAzJ6uafM+scGvSUcw3u79K/bDjymFhVpA/y1jzK+83By+C9ar3iNsY29ssTFpu27hdEZPsjtGptUx7d8MYqHVeASvy1puWWWmc8G61VUMFXpedoIWPJdjx4AfqiJbdoDUnRrYyj3MT+YjFF90k2gxoVw6gQTx+z/e8tNSxzrEVJoADBUY8iUFCYgi+UTBJjtzHYHCFx8JH8jGAcsRne5Ui3OckIxtYXU9Q69gbV0PNPtSISFMQhthIs50HsGLVa+NKxgBLZwIDAQAB";
    /** 前后端约定的RSA密码密文前缀。 */
    public static final String ENCRYPTED_PREFIX = "RSA:";
    /** 每次密码加密均生成独立的256位AES密钥。 */
    private static final int AES_KEY_SIZE = 256;
    public static final String ALGORITHM = "RSA-OAEP-256";
    /** JCE使用的RSA-OAEP算法名称，加密和解密必须保持一致。 */
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    /** RSA密钥长度统一为2048位。 */
    private static final int KEY_SIZE = 2048;
    /** 8192位RSA使用OAEP-SHA256时，单次允许包装的最大明文字节数为256-2*32-2=190。 */
    private static final int MAX_PLAINTEXT_BYTE_LENGTH = 190;
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
     * @return 2048 位 RSA 密钥对
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
     * RSA密钥长度统一为2048位。
     * @return
     */
    public static int getKeySize() {
        PrivateKey privateKey = KEY_PAIR.getPrivate();
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
        return rsaPrivateKey.getModulus().bitLength();
    }

    /**
     * 2048位RSA使用OAEP-SHA256时，单次允许包装的最大明文字节数为2048/8-2*32-2=958。
     * @return
     */
    public static int getMaxPlaintextByteLength() {
        // 混合方案只包装32字节AES密钥，保留该值供公钥接口兼容旧客户端。
        return getKeySize() / 8 - 2 * 32 - 2;
    }

    /**
     * 判断密码是否为当前混合密文或升级前的RSA密文。
     */
    public static boolean isEncrypted(String password) {
        return password != null && password.startsWith(ENCRYPTED_PREFIX);
    }

    /**
     * 使用RSA-OAEP-SHA256公钥包装固定32字节的AES密钥。
     */
    public static byte[] encryptAesKeyByRsa(SecretKey aesKey) throws GeneralSecurityException {
        Cipher rsaCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        rsaCipher.init(Cipher.ENCRYPT_MODE, KEY_PAIR.getPublic(), OAEP_PARAMETER_SPEC);
        return rsaCipher.doFinal(aesKey.getEncoded());
    }

    /**
     * 使用RSA-OAEP-SHA256私钥解包AES密钥，并校验密钥长度。
     */
    public static SecretKey decryptAesKeyByRsa(String encryptedAesKeyBase64) throws GeneralSecurityException {
        byte[] encryptedAesKey = Base64.getDecoder().decode(encryptedAesKeyBase64);
        Cipher rsaCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        rsaCipher.init(Cipher.DECRYPT_MODE, KEY_PAIR.getPrivate(), OAEP_PARAMETER_SPEC);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        if (aesKeyBytes.length != AES_KEY_SIZE / Byte.SIZE) {
            throw new InvalidKeyException("账号密码AES密钥长度不正确");
        }
        return new SecretKeySpec(aesKeyBytes, "AES");
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
        // 超过190位的密码，不加密
        byte[] plainData = plainPassword.getBytes(StandardCharsets.UTF_8);
        if (plainData.length > getMaxPlaintextByteLength()) {
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
