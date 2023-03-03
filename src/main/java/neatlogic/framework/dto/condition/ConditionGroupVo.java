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

package neatlogic.framework.dto.condition;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionGroupVo implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ConditionGroupVo.class);
    private static final long serialVersionUID = 8392325201425982471L;

    private String uuid;
    private List<ConditionVo> conditionList;
    @JSONField(serialize = false)
    private Map<String, ConditionVo> conditionMap;
    private List<ConditionRelVo> conditionRelList;
    @JSONField(serialize = false)
    private List<String> channelUuidList;

    public ConditionGroupVo() {
        super();
    }

    public ConditionGroupVo(JSONObject jsonObj) {
        this.uuid = jsonObj.getString("uuid");
        JSONArray conditionArray = jsonObj.getJSONArray("conditionList");
        if (CollectionUtils.isNotEmpty(conditionArray)) {
            JSONArray channelArray = jsonObj.getJSONArray("channelUuidList");
            if (CollectionUtils.isNotEmpty(channelArray)) {
                channelUuidList = JSONObject.parseArray(channelArray.toJSONString(), String.class);
            }
            conditionList = new ArrayList<>();
            conditionMap = new HashMap<>();
            for (int i = 0; i < conditionArray.size(); i++) {
                JSONObject condition = conditionArray.getJSONObject(i);
                ConditionVo conditionVo = new ConditionVo(condition);
                conditionList.add(conditionVo);
                conditionMap.put(conditionVo.getUuid(), conditionVo);
            }
            JSONArray conditionRelArray = jsonObj.getJSONArray("conditionRelList");
            if (CollectionUtils.isNotEmpty(conditionRelArray)) {
                conditionRelList = new ArrayList<>();
                for (int i = 0; i < conditionRelArray.size(); i++) {
                    JSONObject conditionRel = conditionRelArray.getJSONObject(i);
                    conditionRelList.add(new ConditionRelVo(conditionRel));
                }
            }
        }
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public List<ConditionVo> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<ConditionVo> conditionList) {
        this.conditionList = conditionList;
    }

    public List<ConditionRelVo> getConditionRelList() {
        return conditionRelList;
    }

    public void setConditionRelList(List<ConditionRelVo> conditionRelList) {
        this.conditionRelList = conditionRelList;
    }

    public List<String> getChannelUuidList() {
        return channelUuidList;
    }

    public void setChannelUuidList(List<String> channelUuidList) {
        this.channelUuidList = channelUuidList;
    }

    public Map<String, ConditionVo> getConditionMap() {
        if (MapUtils.isEmpty(conditionMap) && CollectionUtils.isNotEmpty(conditionList)) {
            conditionMap = conditionList.stream().collect(Collectors.toMap(ConditionVo::getUuid, e -> e));
        }
        return conditionMap;
    }

    public String buildScript() {
        if (!CollectionUtils.isEmpty(conditionRelList)) {
            StringBuilder script = new StringBuilder();
            script.append("(");
            String toUuid = null;
            for (ConditionRelVo conditionRelVo : conditionRelList) {
                script.append(getConditionMap().get(conditionRelVo.getFrom()).predicate());
                script.append("and".equals(conditionRelVo.getJoinType()) ? " && " : " || ");
                toUuid = conditionRelVo.getTo();
            }
            script.append(getConditionMap().get(toUuid).predicate());
            script.append(")");
            return script.toString();
        } else {
            ConditionVo conditionVo = conditionList.get(0);
            return String.valueOf(conditionVo.predicate());
        }
    }
}
