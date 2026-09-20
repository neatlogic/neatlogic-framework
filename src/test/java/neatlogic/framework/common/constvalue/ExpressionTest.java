package neatlogic.framework.common.constvalue;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/** 验证条件表达式名称只执行一次国际化转换。 */
public class ExpressionTest {

    /** 已翻译的表达式名称不应再次作为国际化键查询。 */
    @Test
    public void shouldTranslateExpressionNameOnlyOnce() {
        RequestContext context = RequestContext.init((RequestContext) null);
        try {
            context.setLocale(Locale.ENGLISH);
            Assert.assertEquals("Includes", Expression.INCLUDE.getExpressionName());
        } finally {
            context.release();
        }
    }
}
