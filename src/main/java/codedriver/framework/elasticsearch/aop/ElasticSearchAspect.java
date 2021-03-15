package codedriver.framework.elasticsearch.aop;

import codedriver.framework.common.RootComponent;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@RootComponent
/**
 * @Author:chenqiwei
 * @Time:2020年9月30日
 * @ClassName: ElasticSearchAspect
 * @Description: 处理带ESSearch注解的函数
 */
public class ElasticSearchAspect {
    /*private static final ThreadLocal<Map<Long, String>> DOCUMENT_MAP = new ThreadLocal<>();
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchAspect.class);

    @After("@annotation(eSSearch)")
    public void ActionCheck(JoinPoint point, ESSearch eSSearch) {
        Object[] params = point.getArgs();
        if (params == null || params.length == 0) {
            return;
        }
        // 待处理参数列表
        List<Target> targetList = new ArrayList<>();
        
        MethodSignature signature = (MethodSignature)point.getSignature();
        Method method = signature.getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object param = params[i];
            Annotation[] paramAnn = annotations[i];
            // 参数为空或没有注解，直接检查下一个参数
            if (param == null || paramAnn == null || paramAnn.length == 0) {
                continue;
            }
            for (Annotation annotation : paramAnn) {
                // 判断当前注解是否为ESParam，是则加入待处理参数列表
                if (annotation instanceof ESParam) {
                    targetList.add(new Target(((ESParam)annotation).value(), param));
                    break;
                }
            }
        }

        if (CollectionUtils.isNotEmpty(targetList)) {
            Map<Long, String> documentMap = new HashMap<>();
            for (Target target : targetList) {
                // 如果参数本身就是Long类型，则直接使用参数值作为documentId
                if (target.getParam() instanceof Long) {
                    Long documentId = (Long)target.getParam();
                    if (documentId == null) {
                        continue;
                    }
                    documentMap.put(documentId, target.getHandler());
                } else {
                    Object param = target.getParam();
                    //兼容参数是List类型，仅会获取集合里其中一个元素的documentId
                    if(param instanceof List) {
                        List<?> paramList = ((List<?>)param);
                        param = paramList.size()>0?paramList.get(0):param;
                    }
                    Field[] fields = param.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        Long documentId = null;
                        ESKey keyAnnota = field.getAnnotation(ESKey.class);
                        // 需要找到带有annotation并且annotation类型是主键并且值类型是java.lang.Long的属性
                        if (keyAnnota == null || !keyAnnota.type().equals(ESKeyType.PKEY)
                            || !field.getType().equals(Long.class)) {
                            continue;
                        }
                        try {
                            field.setAccessible(true);
                            documentId = (Long)field.get(param);
                            field.setAccessible(false);
                            if (documentId == null) {
                                continue;
                            }
                            documentMap.put(documentId, target.getHandler());
                        } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }

            if (MapUtils.isNotEmpty(documentMap)) {
                if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                    // 没有事务的情况下，跑完一个sql会马上处理document
                    Iterator<Long> itKey = documentMap.keySet().iterator();
                    while (itKey.hasNext()) {
                        Long docId = itKey.next();
                        String handler = documentMap.get(docId);
                        CachedThreadPool.execute(new ElasticSearchHandler(handler, docId));
                    }
                } else {
                    // 有事务的情况下，需要记录有所有documentId以及对应的handler，等事务提交后一起处理
                    Iterator<Long> itKey = documentMap.keySet().iterator();
                    while (itKey.hasNext()) {
                        Long docId = itKey.next();
                        String handler = documentMap.get(docId);

                        Map<Long, String> documentTypeMap = DOCUMENT_MAP.get();
                        if (documentTypeMap == null) {// 第一次进入发现threadlocal为空，初始化threadlocal容器，并且给事务管理器注册回调事件
                            documentTypeMap = new HashMap<>();
                            DOCUMENT_MAP.set(documentTypeMap);

                            TransactionSynchronizationManager
                                .registerSynchronization(new TransactionSynchronizationAdapter() {
                                    @Override
                                    public void afterCommit() {
                                        Map<Long, String> docMap = DOCUMENT_MAP.get();
                                        Iterator<Long> keys = docMap.keySet().iterator();
                                        while (keys.hasNext()) {
                                            Long d = keys.next();
                                            String h = docMap.get(d);
                                            CachedThreadPool.execute(new ElasticSearchHandler(h, d));
                                        }
                                    }

                                    @Override
                                    public void afterCompletion(int status) {
                                        DOCUMENT_MAP.remove();
                                    }
                                });
                        }
                        documentTypeMap.put(docId, handler);
                    }
                }
            }
        }
    }

    // 处理对象
    private class Target {
        private String handler;
        private Object param;

        public Target(String _handler, Object _param) {
            handler = _handler;
            param = _param;
        }

        public String getHandler() {
            return handler;
        }

        public Object getParam() {
            return param;
        }
    }

    private class ElasticSearchHandler extends CodeDriverThread {
        private String handler;
        private Long documentId;

        public ElasticSearchHandler(String _handler, Long _documentId) {
            handler = _handler;
            documentId = _documentId;
        }

        @Override
        protected void execute() {
            String oldName = Thread.currentThread().getName();
            Thread.currentThread().setName("ELASTICSEARCH-DOCUMENT-SAVER-" + documentId);
            IElasticSearchHandler<?, ?> eshandler = ElasticSearchHandlerFactory.getHandler(handler);
            if (eshandler != null) {
                eshandler.save(documentId);
            }
            Thread.currentThread().setName(oldName);
        }

    }*/
}
