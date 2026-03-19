/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.restful.core.privateapi.jsonstream;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import neatlogic.framework.restful.core.ApiComponentTemplateBase;
import neatlogic.framework.restful.dto.ApiVo;

/**
 * JSON 流接口基类。
 * 基于公共模板封装代理调用、参数校验、重提校验、审计和帮助信息，保留 JSONReader 作为第二入参的流式处理语义。
 */
public abstract class JsonStreamApiComponentBase extends ApiComponentTemplateBase implements MyJsonStreamApiComponent {
    // private static Logger logger =
    // LoggerFactory.getLogger(JsonStreamApiComponentBase.class);

    /**
     * JSON 流接口执行模板。
     */
    @Override
    public final Object doService(ApiVo apiVo, JSONObject paramObj, JSONReader jsonReader) throws Exception {
        String error = "";
        Object result = null;
        String param = getAuditParam(apiVo, paramObj);
        long startTime = System.currentTimeMillis();
        // audit.setParam(jsonObj.toString(4));
        try {
            result = invokeWithProxyFallback(
                    (proxy, targetClass) -> executeService(apiVo, paramObj, jsonReader, proxy, targetClass),
                    () -> executeService(apiVo, paramObj, jsonReader, this, this.getClass())
            );
        } catch (Exception e) {
            error = resolveErrorMessage(e);
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            if (shouldAudit(apiVo)) {
                saveAudit(apiVo, JSONObject.parseObject(param), result, error, startTime, endTime);
            }
        }
        return result;
    }

    /*public final String getId() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }*/

    @Override
    public final JSONObject help() {
        return getApiComponentHelp(JSONObject.class, JSONReader.class);
    }

    /**
     * 执行 JSON 流接口的实际服务逻辑。
     */
    private Object executeService(ApiVo apiVo, JSONObject paramObj, JSONReader jsonReader, Object component, Class<?> targetClass) throws Exception {
        validApi(targetClass, paramObj, apiVo, JSONObject.class, JSONReader.class);
        validIsReSubmit(targetClass, apiVo.getToken(), paramObj, JSONObject.class, JSONReader.class);
        return invokeComponentMethod(component, "myDoService", new Class[]{JSONObject.class, JSONReader.class}, paramObj, jsonReader);
    }
}
