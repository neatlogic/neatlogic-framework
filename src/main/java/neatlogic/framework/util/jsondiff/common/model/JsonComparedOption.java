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
import java.util.HashSet;
import java.util.Map;

public class JsonComparedOption {

    /**
     * 忽略数组顺序
     */
    private boolean ignoreOrder;

    /**
     * key 是 actual
     * value 是 expect 映射
     */
    private Map<String, String> mapping;

    /**
     * 忽略的path. 以 . 来区分json层级; 会精准匹配路径
     */
    private HashSet<String> ignorePath;

    /**
     * 忽略的key。不会比较该key
     */
    private HashSet<String> ignoreKey;

    /**
     * 强制使用系统默认比较器
     */
    private boolean mandatoryDefaultNeat = false;


    public JsonComparedOption() {
    }

    public JsonComparedOption setIgnoreOrder(boolean ignoreOrder) {
        this.ignoreOrder = ignoreOrder;
        return this;
    }


    public JsonComparedOption setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
        return this;
    }

    public JsonComparedOption setIgnorePath(HashSet<String> ignorePath) {
        this.ignorePath = ignorePath;
        return this;
    }

    public JsonComparedOption setIgnoreKey(HashSet<String> ignoreKey) {
        this.ignoreKey = ignoreKey;
        return this;
    }

    public boolean isIgnoreOrder() {
        return ignoreOrder;
    }

    public Map<String, String> getMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        return mapping;
    }

    public HashSet<String> getIgnorePath() {
        if (ignorePath == null) {
            ignorePath = new HashSet<>();
        }
        return ignorePath;
    }

    public HashSet<String> getIgnoreKey() {
        if (ignoreKey == null) {
            ignoreKey = new HashSet<>();
        }
        return ignoreKey;
    }

    public boolean isMandatoryDefaultNeat() {
        return mandatoryDefaultNeat;
    }

    public void setMandatoryDefaultNeat(boolean mandatoryDefaultNeat) {
        this.mandatoryDefaultNeat = mandatoryDefaultNeat;
    }
}
