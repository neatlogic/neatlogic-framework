package codedriver.framework.restful.groupsearch.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.constvalue.UserType;
import codedriver.framework.restful.groupsearch.core.IGroupSearchHandler;
@Service
public class CommonGroupHandler implements IGroupSearchHandler {
	@Override
	public String getName() {
		return GroupSearch.COMMON.getValue();
	}

	@Override
	public String getHeader() {
		return GroupSearch.COMMON.getValuePlugin();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> search(JSONObject jsonObj) {
		List<Object> includeList = jsonObj.getJSONArray("includeList");
		if(CollectionUtils.isEmpty(includeList)) {
			includeList = new ArrayList<Object>();
		}
		List<String> includeStrList = includeList.stream().map(object -> object.toString()).collect(Collectors.toList());
		List<String> userTypeList = new ArrayList<String>();
		for (UserType s : UserType.values()) {
			if((s.getIsDefultShow()||(!s.getIsDefultShow()&&includeStrList.contains(getHeader()+s.getValue())))&&s.getText().contains(jsonObj.getString("keyword"))) {
				userTypeList.add(s.getValue());
			}
		}
		return (List<T>) userTypeList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> reload(JSONObject jsonObj) {
		List<String> userTypeList = new ArrayList<String>();
		for(Object value :jsonObj.getJSONArray("valueList")) {
			if(value.toString().startsWith(getHeader())){
				userTypeList.add(value.toString().replace(getHeader(), ""));
			}
		}
		return (List<T>) userTypeList;
	}

	@Override
	public <T> JSONObject repack(List<T> userTypeList) {
		JSONObject userTypeObj = new JSONObject();
		userTypeObj.put("value", "common");
		userTypeObj.put("text", "公共");
		JSONArray userTypeArray = new JSONArray();
		for(T userType : userTypeList) {
			JSONObject userTypeTmp = new JSONObject();
			userTypeTmp.put("value", getHeader()+userType);
			userTypeTmp.put("text", UserType.getText(userType.toString()));
			userTypeArray.add(userTypeTmp);
		}
		userTypeObj.put("sort", getSort());
		userTypeObj.put("dataList", userTypeArray);
		return userTypeObj;
	}

	@Override
	public int getSort() {
		return 0;
	}

	@Override
	public Boolean isLimit() {
		return false;
	}
}
