package codedriver.framework.elasticsearch.core;

import com.alibaba.fastjson.JSONObject;
import com.techsure.multiattrsearch.QueryResultSet;
import com.techsure.multiattrsearch.query.QueryResult;

public interface IElasticSearchHandler {
	
	/**
	 * 
	 * @return
	 */
	public String getDocument();
		
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
	* @demo
	* List<MultiAttrsObject> resultData = result.getData();
	* if (!resultData.isEmpty()) {
        for (MultiAttrsObject el : resultData) {
            el.getId();//documentId
            el.getJSON("键");//根据对应的key，获取value
        }
      }            
	*/
	public <T> QueryResult search(T t);
	
	/**
	 * 
	* @Author 89770
	* @Time 2020年9月27日  
	* @Description: 构建要执行的sql
	* @Param 
	* @return
	 */
	public<T> String myBuildSql(T t);
	
	/**
	 * 
	* @Author 89770
	* @Time 2020年9月24日  
	* @Description: 分批（流式）查询
	* @Param 
	* @return
	* @demo
	* if (resultSet.hasMoreResults()) {
        QueryResult result = resultSet.fetchResult();
        if (!result.getData().isEmpty()) {
            for (MultiAttrsObject el : result.getData()) {
                el.getId();//documentId
                el.getJSON("键");//根据对应的key，获取value
            }
        }
      }
	 */
	public <T> QueryResultSet iterateSearch(T t);
	  
}
