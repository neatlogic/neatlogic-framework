package codedriver.framework.restful.groupsearch.core;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public interface IGroupSearchHandler {
	String getName();
	
	String getHeader();
	
	int getSort();
	
	/**
	 * 是否受总数限制
	 * @return
	 */
	Boolean isLimit();
	
	<T> List<T> search(JSONObject jsonObj);
	
	<T> List<T> reload(JSONObject jsonObj);
	
	<T> JSONObject repack(List<T> dataList);
}
