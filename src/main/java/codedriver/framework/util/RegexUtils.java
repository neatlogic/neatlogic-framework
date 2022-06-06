package codedriver.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {
    private static Logger logger = LoggerFactory.getLogger(RegexUtils.class);
    public static final String NAME = "^[A-Za-z_\\d\\u4e00-\\u9fa5]+$";
    public static final String NAME_WITH_SLASH = "^[A-Za-z_\\d\\u4e00-\\u9fa5/]+$";
    public static final String ENGLISH_NUMBER_NAME = "^[a-zA-Z0-9_\\.]+$";
    public static final String ENGLISH_NAME = "^[A-Za-z]+$";
    public static final String API_TOKEN = "^[A-Za-z_\\{\\}\\d/]+$";
    public static final String DATE_TIME = "[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d";
    public static final String PASSWORD = "^(?!.*[\\u4E00-\\u9FA5\\s])(?!^[a-zA-Z]+$)(?!^[\\d]+$)(?!^[^a-zA-Z\\d]+$)^.{8,20}$";
    public static final String CONNECT_URL = "^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+)|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    private RegexUtils() {
    }

    public static boolean isMatch(String source, String regex) {
        if (StringUtils.isNotEmpty(source) && StringUtils.isNotEmpty(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(source);
            boolean isMatcher = matcher.matches();
            if(!isMatcher){
                logger.error(Thread.currentThread().getStackTrace().toString() + " 字符串不符合sql排序的规范");
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
        return isMatch(source, "^((\\+?-?\\w+(\\.\\w+)?),?\\s*)+\\s+((DESC)|(desc)|(ASC)|(asc))$");

    }
}
