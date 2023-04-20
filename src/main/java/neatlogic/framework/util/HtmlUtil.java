package neatlogic.framework.util;

import neatlogic.framework.dto.UrlInfoVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            str = str.replace("&nbsp;", " ");
            return str;
        }
        return "";
    }

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*?>[\\s\\S]*?</script>", Pattern.CASE_INSENSITIVE);// 定义script的正则表达式
    private static final Pattern STYLE_PATTERN = Pattern.compile("<style[^>]*?>[\\s\\S]*?</style>", Pattern.CASE_INSENSITIVE);// 定义style的正则表达式
    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);// 定义HTML标签的正则表达式
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("&[a-zA-Z]{1,10};", Pattern.CASE_INSENSITIVE);
    //	private static final Pattern IMGSRCPATTREN = Pattern.compile("\"[^<>]*?/api/binary/image/download\\?id=[0-9]*\"");// 定义img标签中src的正则表达式
    private static final Pattern IMGSRC_PATTERN = Pattern.compile("img src=\".*?\"");// 定义img标签中src的正则表达式
    private static final Pattern SRCCONTENT_PATTERN = Pattern.compile("\".*?\"");// 定义img标签中src内容的正则表达式


    public static String removeHtml(String htmlStr) {
        return removeHtml(htmlStr, null);
    }

    public static String removeHtml(String htmlStr, Integer length) {
        Matcher scriptMatcher = SCRIPT_PATTERN.matcher(htmlStr);
        htmlStr = scriptMatcher.replaceAll(""); // 过滤script标签
        Matcher styleMatcher = STYLE_PATTERN.matcher(htmlStr);
        htmlStr = styleMatcher.replaceAll(""); // 过滤style标签
        Matcher htmlMatcher = HTML_PATTERN.matcher(htmlStr);
        htmlStr = htmlMatcher.replaceAll(""); // 过滤html标签
        Matcher specialMatcher = SPECIAL_PATTERN.matcher(htmlStr);
        htmlStr = specialMatcher.replaceAll(""); //过滤特殊字符
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
    public static List<String> getImgSrcList(String htmlStr) {
        List<String> srcList = null;
        if (StringUtils.isNotBlank(htmlStr)) {
            srcList = new ArrayList<>();
            Matcher figureMatcher = IMGSRC_PATTERN.matcher(htmlStr);
            while (figureMatcher.find()) {
                String src = figureMatcher.group();
                Matcher srcContentMatcher = SRCCONTENT_PATTERN.matcher(src);
                if (srcContentMatcher.find()) {
                    srcList.add(srcContentMatcher.group().replaceAll("\"", ""));
                }
            }
        }
        return srcList;
    }

    public static List<UrlInfoVo> getUrlInfoList(String content, String prefix, String suffix) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        List<UrlInfoVo> resultList = new ArrayList<>();
        int prefixIndex = -1;
        int suffixIndex = -1;
        int fromIndex = 0;
        while (fromIndex < content.length()) {
            prefixIndex = content.indexOf(prefix, fromIndex);
            if (prefixIndex == -1) {
                return resultList;
            }
            fromIndex = prefixIndex + prefix.length();
            suffixIndex = content.indexOf(suffix, fromIndex);
            if (suffixIndex == -1) {
                return resultList;
            }
            int beginIndex = prefixIndex + prefix.length();
            resultList.add(new UrlInfoVo(beginIndex, suffixIndex, content.substring(beginIndex, suffixIndex)));
            fromIndex = suffixIndex + suffix.length();
        }
        return resultList;
    }

    public static String urlReplace(String content, List<UrlInfoVo> urlInfoVoList) {
        if (CollectionUtils.isEmpty(urlInfoVoList)) {
            return content;
        }
        int sourceTotalLength = 0;
        int targetTotalLength = 0;
        for (UrlInfoVo urlInfo : urlInfoVoList) {
            String source = urlInfo.getSource();
            String target = AnonymousApiTokenUtil.encrypt(source);
            urlInfo.setTarget(target);
            sourceTotalLength += source.length();
            targetTotalLength += target.length();
        }
        StringBuilder stringBuilder = new StringBuilder(content.length() + targetTotalLength - sourceTotalLength);
        int fromIndex = 0;
        for (UrlInfoVo urlInfoVo : urlInfoVoList) {
            for (int i = fromIndex; i < urlInfoVo.getBeginIndex(); i++) {
                stringBuilder.append(content.charAt(i));
            }
            stringBuilder.append(urlInfoVo.getTarget());
            fromIndex = urlInfoVo.getEndIndex();
        }
        for (int i = fromIndex; i < content.length(); i++) {
            stringBuilder.append(content.charAt(i));
        }
        return stringBuilder.toString();
    }

}
