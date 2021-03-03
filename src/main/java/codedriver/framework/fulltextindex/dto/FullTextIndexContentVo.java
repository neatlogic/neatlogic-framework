package codedriver.framework.fulltextindex.dto;

/**
 * @Title: FullTextIndexContentVo
 * @Package: codedriver.framework.fulltextindex.dto
 * @Description: 目标内容
 * @author: chenqiwei
 * @date: 2021/2/274:17 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class FullTextIndexContentVo {
    private Long targetId;
    private String targetType;
    private String targetField;
    private String content;

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
