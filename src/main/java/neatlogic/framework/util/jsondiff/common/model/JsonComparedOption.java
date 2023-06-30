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
