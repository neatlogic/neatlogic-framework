/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {
    private static final Logger logger = LoggerFactory.getLogger(RegexUtils.class);
    public static final String NAME = "NAME";
    public static final String NAME_WITH_SLASH = "NAME_WITH_SLASH";
    public static final String ENGLISH_NUMBER_NAME = "ENGLISH_NUMBER_NAME";
    public static final String ENGLISH_NAME = "ENGLISH_NAME";
    public static final String API_TOKEN = "API_TOKEN";
    public static final String DATE_TIME = "DATE_TIME";
    public static final String PASSWORD = "PASSWORD";
    public static final String CONNECT_URL = "CONNECT_URL";
    public static final String ORDER_BY = "ORDER_BY";
    public static final String DB_SCHEMA = "DB_SCHEMA";

    public static final Map<String, Pattern> regexPatternMap = new HashMap<String, Pattern>() {
        private static final long serialVersionUID = -960685874597441494L;

        {
            put(NAME, Pattern.compile("^[A-Za-z_\\.\\-\\d\\u4e00-\\u9fa5]+$"));
            put(NAME_WITH_SLASH, Pattern.compile("^[A-Za-z_\\.\\-\\d\\u4e00-\\u9fa5/]+$"));
            put(ENGLISH_NUMBER_NAME, Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$"));
            put(ENGLISH_NAME, Pattern.compile("^[A-Za-z\\.\\-]+$"));
            put(API_TOKEN, Pattern.compile("^[A-Za-z_\\{\\}\\d/]+$"));
            put(DATE_TIME, Pattern.compile("[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d"));
            put(PASSWORD, Pattern.compile("^(?!.*[\\u4E00-\\u9FA5\\s])(?!^[a-zA-Z]+$)(?!^[\\d]+$)(?!^[^a-zA-Z\\d]+$)^.{8,20}$"));
            put(CONNECT_URL, Pattern.compile("^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+)|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?"));
            put(ORDER_BY, Pattern.compile("^((\\+?-?\\w+(\\.\\w+)?),?\\s*)+\\s+((DESC)|(desc)|(ASC)|(asc))$"));
            put(DB_SCHEMA, Pattern.compile("^[\\w-!@#¥`~\\\\|/\\?:;'\"\\$%\\^&\\*\\(\\)\\+=\\{\\}\\[\\]<>]+\\.{1}[\\w-!@#¥`~\\\\|/\\?:;'\"\\$%\\^&\\*\\(\\)\\+=\\{\\}\\[\\]<>]+$"));
        }
    };

    private RegexUtils() {
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

    /**
     * 是否匹配order by后面的字符串规则
     */
    public static boolean isMatchOrderBy(String source) {
        return isMatch(source, ORDER_BY);

    }
}
