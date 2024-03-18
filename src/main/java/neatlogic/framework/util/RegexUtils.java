/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {
    private static final Logger logger = LoggerFactory.getLogger(RegexUtils.class);
    public static final String NAME = "NAME";
    public static final String NAME_WITH_SLASH = "NAME_WITH_SLASH";
    public static final String NAME_WITH_SPACE = "NAME_WITH_SPACE";
    public static final String ENGLISH_NUMBER_NAME = "ENGLISH_NUMBER_NAME";
    public static final String ENGLISH_NAME = "ENGLISH_NAME";
    public static final String ENGLISH_NUMBER_NAME_WHIT_UNDERLINE = "ENGLISH_NUMBER_NAME_WHIT_UNDERLINE";
    public static final String API_TOKEN = "API_TOKEN";
    public static final String DATE_TIME = "DATE_TIME";
    public static final String PASSWORD = "PASSWORD";
    public static final String CONNECT_URL = "CONNECT_URL";
    public static final String ORDER_BY = "ORDER_BY";
    public static final String DB_SCHEMA = "DB_SCHEMA";

    /**
     * 字母
     */
    public static final String UNIQUE_IDENT = "unique_ident";
    /**
     * 小写字母
     */
    public static final String LOWERCASE = "lowercase";
    /**
     * 大写字母
     */
    public static final String UPPERCASE = "uppercase";
    /**
     * 数字
     */
    public static final String NUMBER = "number";
    /**
     * 字母和数字
     */
    public static final String ENCHAR = "enchar";
    /**
     * 邮箱地址
     */
    public static final String MAIL = "mail";
    /**
     * 电话号码
     */
    public static final String PHONE = "phone";
    /**
     * IP地址
     */
    public static final String IP = "ip";
    /**
     * 端口
     */
    public static final String PORT = "port";
    /**
     * URL
     */
    public static final String URL = "url";
    /**
     * markdown文件中的图片链接和跳转链接语法格式匹配
     */
    public static final String MARKDOWN_LINK = "markdown_link";
    /**
     * 配置文件中变量server.host的格式
     */
    public static final String SERVER_HOST = "server_host";

    public static final Map<String, Pattern> regexPatternMap = new HashMap<String, Pattern>() {
        private static final long serialVersionUID = -960685874597441494L;

        {
            put(NAME, Pattern.compile("^[A-Za-z_\\.\\-\\d\\u4e00-\\u9fa5]+$"));
            put(NAME_WITH_SLASH, Pattern.compile("^[A-Za-z_\\.\\-\\d\\u4e00-\\u9fa5/]+$"));
            put(NAME_WITH_SPACE, Pattern.compile("^[A-Za-z_\\.\\-\\d\\s\\u4e00-\\u9fa5]+$"));
            put(ENGLISH_NUMBER_NAME, Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$"));
            put(ENGLISH_NAME, Pattern.compile("^[A-Za-z_\\.\\-]+$"));
            put(ENGLISH_NUMBER_NAME_WHIT_UNDERLINE, Pattern.compile("^[A-Za-z0-9_]+$"));
            put(API_TOKEN, Pattern.compile("^[A-Za-z_\\{\\}\\d/]+$"));
            put(DATE_TIME, Pattern.compile("[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d"));
            put(PASSWORD, Pattern.compile("^(?!.*[\\u4E00-\\u9FA5\\s])(?!^[a-zA-Z]+$)(?!^[\\d]+$)(?!^[^a-zA-Z\\d]+$)^.{8,20}$"));
            put(CONNECT_URL, Pattern.compile("^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+)|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))((:6553[0-5]{1})|(:655[0-2]{1}[0-9]{1})|(:65[0-4]{1}[0-9]{0,2})|(:6[0-4]{1}[0-9]{0,3})|(:[0-5]*[0-9]{0,4}))?(/[a-zA-Z0-9\\&%_\\./-~-]*)?"));
            put(DB_SCHEMA, Pattern.compile("([^.])+\\.([^.])+"));
            put(MARKDOWN_LINK, Pattern.compile("!?\\[.*\\]\\((\\.\\./)*(.+/)*.+\\.\\S+(\\s+\".*\")?\\s*\\)"));

            // 以下正则表达式参考前端TsValidtor.js
            put(UNIQUE_IDENT, Pattern.compile("^[a-zA-Z_][a-zA-Z\\_]*$"));
            put(LOWERCASE, Pattern.compile("^[a-z]*$"));
            put(UPPERCASE, Pattern.compile("^[A-Z]*$"));
            put(NUMBER, Pattern.compile("^[0-9]\\d*$"));
            put(ENCHAR, Pattern.compile("^[a-zA-Z\\d\\.\\_]*$"));
            put(MAIL, Pattern.compile("^[_a-zA-Z0-9-]{1}([\\._a-zA-Z0-9-]+)(\\.[_a-zA-Z0-9-]+)*@[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+){1,3}$"));
            put(PHONE, Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"));
            put(IP, Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$"));
            put(PORT, Pattern.compile("^(6553[0-5]{1}|655[0-2]{1}[0-9]{1}|65[0-4]{1}[0-9]{0,2}|6[0-4]{1}[0-9]{0,3}|[0-5]*[0-9]{0,4})$"));
            put(URL, Pattern.compile("^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+$"));
            put(SERVER_HOST, Pattern.compile("^((http|https)://)([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})((:6553[0-5]{1})|(:655[0-2]{1}[0-9]{1})|(:65[0-4]{1}[0-9]{0,2})|(:6[0-4]{1}[0-9]{0,3})|(:[0-5]*[0-9]{0,4})){1}$"));
        }
    };

    private RegexUtils() {
    }

    public static Pattern getPattern(String key) {
        return regexPatternMap.get(key);
    }

    public static String[] getMatches(String content, String regex, int group) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            if (matcher.groupCount() >= group) {
                matches.add(matcher.group(group));
            }
        }
        return matches.toArray(new String[0]);
    }

    public static void main(String[] a) {
        String[] r = getMatches("#[123]测试内容 http://localhost:8081/develop/rdm.html#/story-detail/9130169265  #[915366961545216]测试内容 http://localhost:8081/develop/rdm.html#/story-detail/913016926568448/913016926568450/915366961545216",
                "#\\[([\\d]+)\\]", 1);
        for (String aa : r) {
            System.out.println(aa);
        }
    }

    public static boolean isMatch(String source, String regex) {
        if (StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(regex)) {
            Pattern pattern = regexPatternMap.get(regex);
            Matcher matcher = pattern.matcher(source);
            boolean isMatcher = matcher.matches();
            if (!isMatcher) {
                logger.error(Arrays.toString(Thread.currentThread().getStackTrace()) + " 字符串不符合sql排序的规范");
            }
            return matcher.matches();
        } else {
            return false;
        }
    }
}
