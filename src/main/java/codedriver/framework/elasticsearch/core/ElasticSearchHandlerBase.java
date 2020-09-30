package codedriver.framework.elasticsearch.core;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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
import codedriver.framework.elasticsearch.constvalue.ESKeyType;
import codedriver.framework.exception.elasticsearch.ElatsticSearchDocumentIdNotFoundException;
import codedriver.framework.exception.tenant.TenantNotFoundException;

public abstract class ElasticSearchHandlerBase<T, R> implements IElasticSearchHandler<T, R> {

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
        return makeupQueryResult(result);
    }
    
    @Override
    public int searchCount(T t) {
        QueryResult result = ESQueryUtil.query(getObjectPool(TenantContext.get().getTenantUuid()), buildSql(t));
        return result.getTotal();
    }

    protected abstract R makeupQueryResult(QueryResult result);

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
    public void save(JSONObject paramObj, String tenantUuid) {
        String documentId = paramObj.getString("&=&" + ESKeyType.PKEY.getValue());
        if (StringUtils.isBlank(documentId)) {
            throw new ElatsticSearchDocumentIdNotFoundException();
        }
        MultiAttrsObjectPool pool = getObjectPool(tenantUuid);
        MultiAttrsObjectPatch patch = pool.save(documentId);
        JSONObject param = mySave(paramObj);
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
            }
        }
        patch.commit();
    }

    @Override
    public void save(JSONObject paramObj) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        if (tenantUuid != null && TenantContext.get() != null) {
            tenantUuid = TenantContext.get().getTenantUuid();
        }
        if (tenantUuid == null) {
            throw new TenantNotFoundException(tenantUuid);
        }
        save(paramObj, tenantUuid);
    }

    public JSONObject mySave(JSONObject paramObj) {
        return paramObj;
    }
}
