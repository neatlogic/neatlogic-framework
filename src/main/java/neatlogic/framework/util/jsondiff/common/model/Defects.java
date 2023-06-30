/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util.jsondiff.common.model;

import java.util.HashMap;

public class Defects {

    /**
     * 期望结果
     */
    private Object expect;

    /**
     * 实际结果
     */
    private Object actual;

    /**
     * 索引地址
     */
    private TravelPath travelPath;

    /**
     * 说明
     */
    private String illustrate;

    /**
     * 附加数据
     */
    private HashMap<String, Object> additionalData;


    public Defects setExpect(Object expect) {
        this.expect = expect;
        return this;
    }

    public Defects setActual(Object actual) {
        this.actual = actual;
        return this;
    }

    public TravelPath getTravelPath() {
        return travelPath;
    }

    public Defects setTravelPath(TravelPath travelPath) {
        this.travelPath = travelPath;
        return this;
    }

    public Defects setIllustrate(String illustrate) {
        this.illustrate = illustrate;
        return this;
    }

    public Defects setIllustrateTemplate(String format, String... args) {
        this.illustrate = String.format(format, args);
        return this;
    }

    public Object getExpect() {
        return expect;
    }

    public Object getActual() {
        return actual;
    }

    public String getIllustrate() {
        return illustrate;
    }

    public HashMap<String, Object> getAdditionalData() {
        return additionalData;
    }

    public Object getAdditionalData(String key) {
        if (additionalData == null) {
            return null;
        }
        return additionalData.get(key);
    }

    public void setAdditionalData(HashMap<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public void pushAdditionalData(String key, Object data) {
        if (this.additionalData == null) {
            this.additionalData = new HashMap<>();
        }
        this.additionalData.put(key, data);
    }
}
