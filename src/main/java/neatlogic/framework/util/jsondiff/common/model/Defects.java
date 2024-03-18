/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
