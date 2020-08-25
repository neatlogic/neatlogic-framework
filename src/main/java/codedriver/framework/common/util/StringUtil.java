package codedriver.framework.common.util;

public class StringUtil {
	public static String removeHtml(String sourceContent) {
		String content = "";
		if (sourceContent != null) {
			content = sourceContent.replaceAll("</?[^>]+>", "");
		}
		return content;
	}
}
