package codedriver.framework.fulltextindex.core;

/**
 * @Title: IFullIndexDocumentType
 * @Package: codedriver.framework.fulltextindex.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/2/255:02 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface IFullTextIndexType {
    String getType();

    String getTypeName();

    String getTypeName(String type);
}
