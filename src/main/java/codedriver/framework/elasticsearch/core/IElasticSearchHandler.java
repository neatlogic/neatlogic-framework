package codedriver.framework.elasticsearch.core;

import com.alibaba.fastjson.JSONObject;
import com.techsure.multiattrsearch.QueryResultSet;

public interface IElasticSearchHandler<T, R> {

    /**
     * 
     * @return
     */
    public String getDocument();

    /**
     * 保存、修改
     */
    public void save(JSONObject paramObj, String tenant);

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
     * @demo List<MultiAttrsObject> resultData = result.getData(); if (!resultData.isEmpty()) { for (MultiAttrsObject el
     *       : resultData) { el.getId();//documentId el.getJSON("键");//根据对应的key，获取value } }
     */
    public R search(T target);
    
    /**
     * 
    * @Author: chenqiwei
    * @Time:2020年9月29日
    * @Description: 返回查询结果数量 
    * @param @param target
    * @param @return 
    * @return int
     */
    public int searchCount(T target);

    /**
     * 
     * @Author 89770
     * @Time 2020年9月24日
     * @Description: 分批（流式）查询
     * @Param
     * @return
     * @demo if (resultSet.hasMoreResults()) { QueryResult result = resultSet.fetchResult(); if
     *       (!result.getData().isEmpty()) { for (MultiAttrsObject el : result.getData()) { el.getId();//documentId
     *       el.getJSON("键");//根据对应的key，获取value } } }
     */
    public QueryResultSet iterateSearch(T target);

}
