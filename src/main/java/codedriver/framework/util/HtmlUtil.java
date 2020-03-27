package codedriver.framework.util;

public class HtmlUtil {
	public static String encodeHtml(String str) {
		if (str != null && !"".equals(str)) {
			str = str.replace("&", "&amp;");
			str = str.replace("<", "&lt;");
			str = str.replace(">", "&gt;");
			str = str.replace("'", "&#39;");
			str = str.replace("\"", "&quot;");
			return str;
		}
		return "";
	}
}
