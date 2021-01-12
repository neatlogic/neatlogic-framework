package codedriver.framework.fullindex.aop;

import codedriver.framework.common.RootComponent;
import codedriver.framework.fullindex.annotation.FullIndex;
import codedriver.framework.fullindex.annotation.FullIndexContent;
import codedriver.framework.fullindex.core.FullIndexUtil;
import codedriver.framework.fullindex.dao.mapper.WordMapper;
import codedriver.framework.fullindex.dto.WordVo;
import codedriver.framework.transaction.core.AfterTransactionJob;
import codedriver.framework.transaction.core.ICommitted;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Title: FullIndexAspect
 * @Package: codedriver.framework.fullindex.aop
 * @Description: 创建全文索引的切片
 * @author: chenqiwei
 * @date: 2021/1/712:21 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Aspect
@RootComponent
public class FullIndexAspect {
    private static final ThreadLocal<Map<Long, String>> DOCUMENT_MAP = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(FullIndexAspect.class);

    @Autowired
    private WordMapper wordMapper;

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
        AfterTransactionJob<String> committer = new AfterTransactionJob<>();
        committer.execute(content, new ICommitted<String>() {
            @Override
            public void execute(String s) {
                if (StringUtils.isNotBlank(s)) {
                    try {
                        List<WordVo> wordList = FullIndexUtil.sliceWord(s);
                        for (WordVo word : wordList) {
                            wordMapper.insertWordbook(word);
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        System.out.println(content);
    }


}
