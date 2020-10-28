package codedriver.framework.elasticsearch.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.techsure.multiattrsearch.MultiAttrsObjectPatch;
import com.techsure.multiattrsearch.MultiAttrsObjectPool;
import com.techsure.multiattrsearch.MultiAttrsQuery;
import com.techsure.multiattrsearch.QueryResultSet;
import com.techsure.multiattrsearch.query.QueryParser;
import com.techsure.multiattrsearch.query.QueryResult;
import com.techsure.multiattrsearch.util.ESQueryUtil;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.elasticsearch.dao.mapper.ElasticSearchMapper;
import codedriver.framework.elasticsearch.dto.ElasticSearchAuditVo;
import codedriver.framework.exception.elasticsearch.ElatsticSearchDocumentIdNotFoundException;
import codedriver.framework.exception.tenant.TenantNotFoundException;

public abstract class ElasticSearchHandlerBase<T, R> implements IElasticSearchHandler<T, R> {
    
    private static ElasticSearchMapper elasticSearchMapper;

    @Autowired
    private void setReminderMapper(ElasticSearchMapper _elasticSearchMapper) {
        elasticSearchMapper = _elasticSearchMapper;
    }

    protected MultiAttrsObjectPool objectPool;

    protected MultiAttrsObjectPool getObjectPool(String tenant) {
        if (objectPool == null) {
            objectPool = ElasticSearchPoolManager.getObjectPool(this.getDocument());
        }
        if (objectPool != null) {
            objectPool.checkout(tenant);

        }
        return objectPool;
    }

    /**
     * 
     * @Author 89770
     * @Time 2020年9月27日
     * @Description: 构建要执行的sql
     * @Param
     * @return
     */
    protected abstract String buildSql(T target);

    @Override
    public R search(T t) {
        QueryResult result = ESQueryUtil.query(getObjectPool(TenantContext.get().getTenantUuid()), buildSql(t));
        return makeupQueryResult(t, result);
    }

    @Override
    public int searchCount(T t) {
        QueryResult result = ESQueryUtil.query(getObjectPool(TenantContext.get().getTenantUuid()), buildSql(t));
        return result.getTotal();
    }

    protected abstract R makeupQueryResult(T t, QueryResult result);

    @Override
    public QueryResultSet iterateSearch(T t) {
        QueryParser parser = getObjectPool(TenantContext.get().getTenantUuid()).createQueryParser();
        MultiAttrsQuery query = parser.parse(buildSql(t));
        return query.iterate();
    }

    @Override
    public void delete(String documentId) {
        getObjectPool(TenantContext.get().getTenantUuid()).delete(documentId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void save(Long documentId, String tenantUuid) {
        if (documentId == null) {
            throw new ElatsticSearchDocumentIdNotFoundException();
        }
        MultiAttrsObjectPool pool = getObjectPool(tenantUuid);
        MultiAttrsObjectPatch patch = pool.save(documentId.toString());
        JSONObject param = mySave(documentId);
        if (param.isEmpty()) {
            return;
        }
        Set<Entry<String, Object>> entrySet = param.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                patch.set(entry.getKey(), JSONObject.parseObject(value.toString()));
            } else if (value instanceof JSONArray) {
                patch.set(entry.getKey(), JSONArray.parseArray(value.toString()));
            } else if (value instanceof String) {
                patch.set(entry.getKey(), value.toString());
            } else if (value instanceof List) {
                patch.setStrings(entry.getKey(), (List)value);
            } else if (value instanceof Double) {
                patch.set(entry.getKey(), Double.valueOf(value.toString()));
            } else if (value instanceof Integer) {
                patch.set(entry.getKey(), Integer.valueOf(value.toString()));
            } else if (value instanceof Boolean) {
                patch.set(entry.getKey(), Boolean.valueOf(value.toString()));
            } else if (value instanceof Long) {
                patch.set(entry.getKey(), Long.valueOf(value.toString()));
            }
        }
        patch.commit();
    }

    @Override
    public void save(Long documentId) {
        ElasticSearchAuditVo elasticSeachAduitVo = new ElasticSearchAuditVo(this.getDocument(), documentId);
        try {
            elasticSearchMapper.insertElasticSearchAudit(elasticSeachAduitVo);
            String tenantUuid = TenantContext.get().getTenantUuid();
            if (tenantUuid != null && TenantContext.get() != null) {
                tenantUuid = TenantContext.get().getTenantUuid();
            }
            if (tenantUuid == null) {
                throw new TenantNotFoundException(tenantUuid);
            }
            save(documentId, tenantUuid);
            elasticSearchMapper.deleteElasticSearchAuditByDocumentId(documentId);
        }catch(Exception ex) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            ex.fillInStackTrace().printStackTrace(printWriter);
            elasticSeachAduitVo.setErrorMsg(result.toString());
            elasticSearchMapper.insertElasticSearchAudit(elasticSeachAduitVo);
        }
    }

    public abstract JSONObject mySave(Long documentId);
}
