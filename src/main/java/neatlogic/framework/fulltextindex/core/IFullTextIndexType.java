/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.fulltextindex.core;

public interface IFullTextIndexType {
    String getType();

    String getTypeName();

    String getTypeName(String type);


    //是否允许在搜索中心中出现
    boolean isActiveGlobalSearch();
}
