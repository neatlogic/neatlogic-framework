package codedriver.framework.common.util;

public class StringUtil {
	/**
	 * @Description: 去掉html标签
	 * @Date: 2021/2/19 12:16
	 * @Params: [sourceContent]
	 * @Returns: java.lang.String
	 **/
	public static String removeHtml(String sourceContent) {
		String content = "";
		if (sourceContent != null) {
			content = sourceContent.replaceAll("</?[^>]+>", "");
		}
		return content;
	}

	/**
	 * @Description: 将字符串的首字母转大写
	 * @Author: 89770
	 * @Date: 2021/2/19 12:17
	 * @Params: str 需要转换的字符串
	 * @Returns: * @return: null
	 **/
	public static String toFirstCharUpperCase(String str) {
		// 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
		char[] cs=str.toCharArray();
		cs[0]-=32;
		return String.valueOf(cs);
	}
}
