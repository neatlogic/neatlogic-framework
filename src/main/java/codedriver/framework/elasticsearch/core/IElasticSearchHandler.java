package codedriver.framework.elasticsearch.core;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.techsure.multiattrsearch.query.QueryResult;

public interface IElasticSearchHandler {
	
	/**
	 * 
	 * @return
	 */
	public String getDocument();
	
	/**
	 * name
	 * @return
	 */
	public String getDocumentName();
	
	/**
     * id
     * @return
     */
    public String getDocumentId();
	
	/**
	 * 保存、修改
	 */
	public void save(JSONObject paramObj,String tenant);
	
	/**
     * 保存、修改
     */
	public void save(JSONObject paramObj);
	
	/**
	 * 
	* @Author 89770
	* @Time 2020年9月23日  
	* @Description: 删除
	* @Param 
	* @return
	 */
	public void delete(String documentId);
	
	/**
	 * 
	* @Author 89770
	* @Time 2020年9月23日  
	* @Description: 查询  
	* @Param 
	* @return
	 */
	public QueryResult search(String sql);
	
	/**
	 * 执行动作
	 */
	public JSONObject getConfig(List<Object> paramList);

   
}
