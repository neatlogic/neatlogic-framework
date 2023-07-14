/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.common.util;

public class RC4Util {

    private static final String KEY = "r3MQiqvyDaEocb4zl8YJ3ebbZcpKoo7E";

    private static final String PRE_TAGENT = "{ENCRYPTED}";

    public static final String PRE_OLD = "RC4:";

    public static final String PRE = "{RC4}";

    public static String encrypt(final String plaintext) {
        if (!plaintext.startsWith(PRE) && !plaintext.startsWith(PRE_OLD)) {
            return PRE + byte2HexStr(encrypt(KEY.getBytes(), plaintext.getBytes()));
        }
        return plaintext;
    }

    public static String decrypt(String ciphertext) {
        if (ciphertext.startsWith(PRE)) {
            ciphertext = ciphertext.substring(5);
        } else if (ciphertext.startsWith(PRE_OLD)) {
            ciphertext = ciphertext.substring(4);
        } else if (ciphertext.startsWith(PRE_TAGENT)) {
            ciphertext = ciphertext.substring(11);
        } else {
            return ciphertext;
        }
        return new String(decrypt(KEY.getBytes(), hexStr2Bytes(ciphertext)));
    }

    /*public static String encrypt(final String key, final String plaintext) {
        if (!plaintext.startsWith(PRE) && !plaintext.startsWith(PRE_OLD)) {
            return byte2HexStr(encrypt(key.getBytes(), plaintext.getBytes()));
        }
        return plaintext;
    }*/

    public static String decrypt(final String key, String ciphertext) {
        if (ciphertext.startsWith(PRE)) {
            ciphertext = ciphertext.substring(5);
        } else if (ciphertext.startsWith(PRE_OLD)) {
            ciphertext = ciphertext.substring(4);
        } else if (ciphertext.startsWith(PRE_TAGENT)) {
            ciphertext = ciphertext.substring(11);
        } else {
            return ciphertext;
        }
        return new String(decrypt(key.getBytes(), hexStr2Bytes(ciphertext)));
    }

    private static byte[] encrypt(final byte[] key, final byte[] plaintext) {
        final byte[] S = new byte[256];
        final byte[] T = new byte[256];
        final int keylen;

        if (key.length < 1 || key.length > 256) {
            throw new IllegalArgumentException("key must be between 1 and 256 bytes");
        } else {
            keylen = key.length;
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % keylen];
            }
            int j = 0;
            byte tmp;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                tmp = S[j];
                S[j] = S[i];
                S[i] = tmp;
            }
        }

        final byte[] ciphertext = new byte[plaintext.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        for (int counter = 0; counter < plaintext.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            ciphertext[counter] = (byte) (plaintext[counter] ^ k);
        }
        return ciphertext;
    }

    private static byte[] decrypt(final byte[] key, final byte[] ciphertext) {
        return encrypt(key, ciphertext);
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    private static String byte2HexStr(byte[] b) {
        String tmp;
        StringBuilder sb = new StringBuilder();
        for (byte value : b) {
            tmp = Integer.toHexString(value & 0xFF);
            sb.append((tmp.length() == 1) ? "0" + tmp : tmp);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    private static byte[] hexStr2Bytes(String src) {
        int m, n;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (byte) Integer.parseInt(src.substring(i * 2, m) + src.substring(m, n), 16);
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(RC4Util.encrypt("123456Ab"));
    }
}
