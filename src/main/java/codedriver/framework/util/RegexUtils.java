package codedriver.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegexUtils {
    private static Logger logger = LoggerFactory.getLogger(RegexUtils.class);

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
