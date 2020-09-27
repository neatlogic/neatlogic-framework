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
import codedriver.framework.exception.tenant.TenantNotFoundException;

public abstract class EsHandlerBase implements IElasticSearchHandler {

    protected MultiAttrsObjectPool objectPool;

    protected MultiAttrsObjectPool getObjectPool(String poolName, String tenant) {
        if (objectPool == null) {
            objectPool = ElasticSearchPoolManager.getObjectPool(poolName);
        }
        if (objectPool != null) {
            if (StringUtils.isNotBlank(tenant)) {
                objectPool.checkout(tenant);
            } else {
                objectPool.checkout(TenantContext.get().getTenantUuid());
            }
        }
        return objectPool;
    }

    @Override
    public<T> QueryResult search(T t) {
        return ESQueryUtil.query(ElasticSearchPoolManager.getObjectPool(this.getDocument()), myBuildSql(t));
    }
    
    @Override
    public<T> QueryResultSet iterateSearch(T t) {
        QueryParser parser = ElasticSearchPoolManager.getObjectPool(this.getDocument()).createQueryParser();
        MultiAttrsQuery query = parser.parse(myBuildSql(t));
        return query.iterate();
    }

    @Override
    public void delete(String documentId) {
        ElasticSearchPoolManager.getObjectPool(this.getDocument()).delete(this.getDocumentId());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void save(JSONObject paramObj, String tenantUuid) {
        MultiAttrsObjectPool pool = getObjectPool(this.getDocument(), tenantUuid);
        MultiAttrsObjectPatch patch = pool.save(this.getDocumentId());
        if (paramObj == null) {
            delete(this.getDocumentId());
        } else {
            JSONObject param = mySave(paramObj);
            Set<Entry<String, Object>> entrySet = param.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                Object value = entry.getValue();
                if (value instanceof JSONObject) {
                    patch.set(entry.getKey(), JSONObject.parseObject(value.toString()));
                } else if (value instanceof JSONArray) {
                    patch.set(entry.getKey(), JSONArray.parseObject(value.toString()));
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
    }

    @Override
    public void save(JSONObject paramObj) {
        String tenantUuid = null;
        tenantUuid = paramObj.getString("tenantUuid");
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
