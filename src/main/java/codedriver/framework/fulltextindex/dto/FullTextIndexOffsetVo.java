package codedriver.framework.fulltextindex.dto;

import java.util.Objects;

/**
 * @Title: FullTextIndexOffsetVo
 * @Package: codedriver.framework.fulltextindex.dto
 * @Description: 分词偏移实体
 * @author: chenqiwei
 * @date: 2021/2/255:50 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class FullTextIndexOffsetVo {
    private Long fieldId;
    private int start;
    private int end;

    public FullTextIndexOffsetVo() {

    }

    public FullTextIndexOffsetVo(int _start, int _end) {
        this.start = _start;
        this.end = _end;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullTextIndexOffsetVo that = (FullTextIndexOffsetVo) o;
        return start == that.start && end == that.end && fieldId.equals(that.fieldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldId, start, end);
    }
}
