package codedriver.framework.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class HtmlUtil {
	public static String encodeHtml(String str) {
		if (StringUtils.isNotBlank(str)) {
			str = str.replace("&", "&amp;");
			str = str.replace("<", "&lt;");
			str = str.replace(">", "&gt;");
			str = str.replace("'", "&#39;");
			str = str.replace("\"", "&quot;");
			return str;
		}
		return "";
	}

	public static String decodeHtml(String str) {
		if (StringUtils.isNotBlank(str)) {
			str = str.replace("&quot;", "\"");
			str = str.replace("&#39;", "'");
			str = str.replace("&gt;", ">");
			str = str.replace("&lt;", "<");
			str = str.replace("&amp;", "&");
			return str;
		}
		return "";
	}

	private static final Pattern SCRIPTPATTREN = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>");// 定义script的正则表达式
	private static final Pattern STYLEPATTREN = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>");// 定义style的正则表达式
	private static final Pattern HTMLPATTREN = Pattern.compile("<[^>]+>");// 定义HTML标签的正则表达式
//	private static final Pattern IMGSRCPATTREN = Pattern.compile("\"[^<>]*?/api/binary/image/download\\?id=[0-9]*\"");// 定义img标签中src的正则表达式
	private static final Pattern IMGSRCPATTREN = Pattern.compile("img src=\".*?\"");// 定义img标签中src的正则表达式
	private static final Pattern SRCCONTENTPATTREN = Pattern.compile("\".*?\"");// 定义img标签中src内容的正则表达式

	public static String removeHtml(String htmlStr, Integer length) {
		Matcher m_script = SCRIPTPATTREN.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签
		Matcher m_style = STYLEPATTREN.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签
		Matcher m_html = HTMLPATTREN.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签
		htmlStr = htmlStr.replaceAll("&nbsp;"," ");//转义空格符
		if (length != null && length > 0) {
			if (htmlStr.length() <= length) {
				return htmlStr;
			} else {
				htmlStr = htmlStr.substring(0, length) + "...";
			}
		}
		return htmlStr;
	}

	/**
	 * @Description: 提取htmlStr中所有的图片地址
	 * @Author: laiwt
	 * @Date: 2021/1/25 15:01
	 * @Params: [htmlStr]
	 * @Returns: java.util.List<java.lang.String>
	**/
	public static List<String> getImgSrcList(String htmlStr){
		List<String> srcList = null;
		if(StringUtils.isNotBlank(htmlStr)){
			srcList = new ArrayList<>();
			Matcher figureMatcher = IMGSRCPATTREN.matcher(htmlStr);
			while (figureMatcher.find()){
				String src = figureMatcher.group();
				Matcher srcContentMatcher = SRCCONTENTPATTREN.matcher(src);
				if(srcContentMatcher.find()){
					srcList.add(srcContentMatcher.group().replaceAll("\"",""));
				}
			}
		}
		return srcList;
	}

}
