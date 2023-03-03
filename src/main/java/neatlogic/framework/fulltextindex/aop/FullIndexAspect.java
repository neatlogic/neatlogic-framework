package neatlogic.framework.fulltextindex.aop;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.fulltextindex.annotation.FullIndex;
import neatlogic.framework.fulltextindex.annotation.FullIndexContent;
import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Title: FullIndexAspect
 * @Package: neatlogic.framework.fullindex.aop
 * @Description: 创建全文索引的切片
 * @author: chenqiwei
 * @date: 2021/1/712:21 下午
 **/
@Aspect
@RootComponent
public class FullIndexAspect {
    private static final ThreadLocal<Map<Long, String>> DOCUMENT_MAP = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(FullIndexAspect.class);

    @Autowired
    private FullTextIndexMapper fullTextIndexMapper;

    @After("@annotation(fullIndex)")
    public void createFullIndex(JoinPoint point, FullIndex fullIndex) {
        Object[] params = point.getArgs();
        if (params == null || params.length == 0) {
            return;
        }

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();
        String content = "";
        for (int i = 0; i < annotations.length; i++) {
            Object param = params[i];
            Annotation[] paramAnn = annotations[i];
            // 参数为空或没有注解，直接检查下一个参数
            if (param == null || paramAnn == null || paramAnn.length == 0) {
                continue;
            }
            for (Annotation annotation : paramAnn) {
                //content参数
                if (annotation instanceof FullIndexContent && param instanceof String) {
                    content = param.toString();
                    break;
                }
            }
        }

        System.out.println(content);
    }


}
