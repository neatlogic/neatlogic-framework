/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.fulltextindex.core;


import neatlogic.framework.fulltextindex.dto.globalsearch.DocumentVo;

public interface IFullTextIndexHandler {

    IFullTextIndexType getType();


    /*
     * @Description: 创建索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 4:56 下午
     * @Params: [target, isAsync]
     * @Returns: void
     **/
    void createIndex(Long targetId);

    /*
     * @Description: 删除索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 5:03 下午
     * @Params: [targetId]
     * @Returns: void
     **/
    void deleteIndex(Long targetId);

    /*
     * @Description: 重建索引
     * @Author: chenqiwei
     * @Date: 2021/2/25 5:03 下午
     * @Params: [isRebuildAll]
     * @Returns: void
     **/
    void rebuildIndex(String type, Boolean isRebuildAll);

    /**
     * 格式化搜索中心搜索结果
     *
     * @param documentVo 原始搜索结果
     */
    void makeupDocument(DocumentVo documentVo);
}
