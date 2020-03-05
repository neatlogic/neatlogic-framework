package codedriver.framework.common.util;

public class RC4Util {

	private static final String RC4_KEY = "techsure";

	public static String encrypt(final String plaintext) {
		return byte2HexStr(encrypt(RC4_KEY.getBytes(), plaintext.getBytes()));
	}

	public static String decrypt(final String ciphertext) {
		return new String(decrypt(RC4_KEY.getBytes(), hexStr2Bytes(ciphertext)));
	}

	public static String encrypt(final String key, final String plaintext) {
		return byte2HexStr(encrypt(key.getBytes(), plaintext.getBytes()));
	}

	public static String decrypt(final String key, final String ciphertext) {
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
	 * @param byte[]
	 *            b byte数组
	 * @return String 每个Byte值之间空格分隔
	 */
	private static String byte2HexStr(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	private static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = (byte) Integer.parseInt(src.substring(i * 2, m) + src.substring(m, n), 16);
		}
		return ret;
	}
}
