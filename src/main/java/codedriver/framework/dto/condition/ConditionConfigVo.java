package codedriver.framework.dto.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.dto.BasePageVo;

public class ConditionConfigVo extends BasePageVo implements Serializable {

	private static final long serialVersionUID = 5439300427812355573L;

	private List<ConditionGroupVo> conditionGroupList;
	private transient Map<String, ConditionGroupVo> conditionGroupMap;
	private List<ConditionGroupRelVo> conditionGroupRelList;
	public ConditionConfigVo() {
	}

	public ConditionConfigVo(JSONObject jsonObj) {
		JSONArray conditionGroupArray = jsonObj.getJSONArray("conditionGroupList");
		if (CollectionUtils.isNotEmpty(conditionGroupArray)) {
			
			conditionGroupList = new ArrayList<ConditionGroupVo>();
			conditionGroupMap = new HashMap<String, ConditionGroupVo>();
			for (int i = 0; i < conditionGroupArray.size(); i++) {
				JSONObject conditionGroupJson = conditionGroupArray.getJSONObject(i);
				ConditionGroupVo conditionGroupVo = new ConditionGroupVo(conditionGroupJson);
				conditionGroupList.add(conditionGroupVo);
				conditionGroupMap.put(conditionGroupVo.getUuid(), conditionGroupVo);
			}
			JSONArray conditionGroupRelArray = jsonObj.getJSONArray("conditionGroupRelList");
			if (CollectionUtils.isNotEmpty(conditionGroupRelArray)) {
				conditionGroupRelList = new ArrayList<ConditionGroupRelVo>();
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
		return conditionGroupMap;
	}

	public void setConditionGroupMap(Map<String, ConditionGroupVo> conditionGroupMap) {
		this.conditionGroupMap = conditionGroupMap;
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
				script.append(conditionGroupMap.get(conditionGroupRelVo.getFrom()).buildScript());
				script.append("and".equals(conditionGroupRelVo.getJoinType()) ? " && " : " || ");
				toUuid = conditionGroupRelVo.getTo();
			}
			script.append(conditionGroupMap.get(toUuid).buildScript());
			script.append(")");
			return script.toString();
		} else {
			ConditionGroupVo conditionGroupVo = conditionGroupList.get(0);
			return conditionGroupVo.buildScript();
		}
	}
}
