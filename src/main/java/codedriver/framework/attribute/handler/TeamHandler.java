package codedriver.framework.attribute.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.attribute.constvalue.AttributeHandler;
import codedriver.framework.attribute.core.IAttributeHandler;
import codedriver.framework.attribute.dto.AttributeDataVo;
import codedriver.framework.attribute.dto.AttributeVo;
import codedriver.framework.attribute.exception.AttributeValidException;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dto.TeamVo;

@Component
public class TeamHandler implements IAttributeHandler {

	@Autowired
	private TeamMapper teamMapper;

	@Override
	public String getType() {
		return AttributeHandler.TEAM.getValue();
	}

	@Override
	public boolean valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
		return true;
	}

	@Override
	public String getConfigPage() {
		return "process.attribute.handler.team.teamconfig";
	}

	@Override
	public String getInputPage() {
		return "process.attribute.handler.team.teaminput";
	}

	@Override
	public String getViewPage() {
		return "process.attribute.handler.team.teamview";
	}

	@Override
	public Object getData(AttributeVo attributeVo, Map<String, String[]> paramMap) {
		TeamVo teamVo = new TeamVo();
		teamVo.setPageSize(20);
		if (paramMap.containsKey("name") && paramMap.get("name").length == 1) {
			teamVo.setName(paramMap.get("name")[0]);
		}
		List<TeamVo> teamList = teamMapper.searchTeam(teamVo);
		JSONArray returnList = new JSONArray();
		for (TeamVo team : teamList) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("value", team.getUuid());
			jsonObj.put("text", team.getName());
			returnList.add(jsonObj);
		}
		return returnList;
	}

	@Override
	public List<String> getValueList(Object data) {
		List<String> valueList = new ArrayList<>();
		try {
			JSONObject jsonObj = JSONObject.parseObject(data.toString());
			if (jsonObj.containsKey("value")) {
				valueList.add(jsonObj.getString("value"));
			}
		} catch (Exception ex) {

		}
		return valueList;
	}

	@Override
	public String getText(Object data) {
		try {
			JSONObject jsonObj = JSONObject.parseObject(data.toString());
			if (jsonObj.containsKey("text")) {
				return jsonObj.getString("text");
			}
		} catch (Exception ex) {

		}
		return "";
	}

}
