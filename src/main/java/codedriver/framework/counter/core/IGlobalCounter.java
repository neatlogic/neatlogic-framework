package codedriver.framework.counter.core;

/**
* @Author:chenqiwei
* @Time:Sep 10, 2019
* @ClassName: IGlobalCounter 
* @Description: 右上角的全局统计信息接口
 */
public interface IGlobalCounter {

    /**
    * @Description: 展示名称
    * @Param: []
    * @return: java.lang.String
    */
    String getName();

    /**
    * @Description: 插件类路径（插件ID）
    * @Param: []
    * @return: java.lang.String
    */
    String getPluginId();

    /** 
    * @Description: 预览图片路径
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getPreviewPath();

    /** 
    * @Description: 描述 
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getDescription();

    /** 
    * @Description: 模板路径 
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getShowTemplate();

    /** 
    * @Description: 获取展示数据
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    Object getShowData();
}
