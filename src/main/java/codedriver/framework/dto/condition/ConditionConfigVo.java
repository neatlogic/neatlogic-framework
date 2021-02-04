package codedriver.framework.dto.condition;

import codedriver.framework.common.dto.BasePageVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionConfigVo extends BasePageVo implements Serializable {

    private static final long serialVersionUID = 5439300427812355573L;

    protected List<ConditionGroupVo> conditionGroupList = new ArrayList<ConditionGroupVo>();;
    @JSONField(serialize = false)
    private transient Map<String, ConditionGroupVo> conditionGroupMap ;
    protected List<ConditionGroupRelVo> conditionGroupRelList = new ArrayList<ConditionGroupRelVo>();;

    public ConditionConfigVo() {}

    public ConditionConfigVo(JSONObject jsonObj) {
        JSONArray conditionGroupArray = jsonObj.getJSONArray("conditionGroupList");
        if (CollectionUtils.isNotEmpty(conditionGroupArray)) {
            conditionGroupMap = new HashMap<String, ConditionGroupVo>();
            for (int i = 0; i < conditionGroupArray.size(); i++) {
                JSONObject conditionGroupJson = conditionGroupArray.getJSONObject(i);
                ConditionGroupVo conditionGroupVo = new ConditionGroupVo(conditionGroupJson);
                conditionGroupList.add(conditionGroupVo);
                conditionGroupMap.put(conditionGroupVo.getUuid(), conditionGroupVo);
            }
            JSONArray conditionGroupRelArray = jsonObj.getJSONArray("conditionGroupRelList");
            if (CollectionUtils.isNotEmpty(conditionGroupRelArray)) {
                for (int i = 0; i < conditionGroupRelArray.size(); i++) {
                    JSONObject conditionRelGroup = conditionGroupRelArray.getJSONObject(i);
                    conditionGroupRelList.add(new ConditionGroupRelVo(conditionRelGroup));
                }
            }
        }
    }

    public List<ConditionGroupVo> getConditionGroupList() {
        return conditionGroupList;
    }

    public void setConditionGroupList(List<ConditionGroupVo> conditionGroupList) {
        this.conditionGroupList = conditionGroupList;
    }

    public Map<String, ConditionGroupVo> getConditionGroupMap() {
        if (MapUtils.isEmpty(conditionGroupMap) && CollectionUtils.isNotEmpty(conditionGroupList)) {
            conditionGroupMap = conditionGroupList.stream().collect(Collectors.toMap(e -> e.getUuid(), e -> e));
        }
        return conditionGroupMap;
    }

    public List<ConditionGroupRelVo> getConditionGroupRelList() {
        return conditionGroupRelList;
    }

    public void setConditionGroupRelList(List<ConditionGroupRelVo> conditionGroupRelList) {
        this.conditionGroupRelList = conditionGroupRelList;
    }

    public String buildScript() {
        if (CollectionUtils.isNotEmpty(conditionGroupRelList)) {
            StringBuilder script = new StringBuilder();
            script.append("(");
            String toUuid = null;
            for (ConditionGroupRelVo conditionGroupRelVo : conditionGroupRelList) {
                script.append(getConditionGroupMap().get(conditionGroupRelVo.getFrom()).buildScript());
                script.append("and".equals(conditionGroupRelVo.getJoinType()) ? " && " : " || ");
                toUuid = conditionGroupRelVo.getTo();
            }
            script.append(getConditionGroupMap().get(toUuid).buildScript());
            script.append(")");
            return script.toString();
        } else {
            ConditionGroupVo conditionGroupVo = conditionGroupList.get(0);
            return conditionGroupVo.buildScript();
        }
    }
}
