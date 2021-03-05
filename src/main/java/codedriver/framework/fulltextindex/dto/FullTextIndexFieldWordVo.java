package codedriver.framework.fulltextindex.dto;

import codedriver.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @Title: FullTextIndexFieldVo
 * @Package: codedriver.framework.fulltextindex.dto
 * @Description: 索引字段实体类
 * @author: chenqiwei
 * @date: 2021/2/255:47 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class FullTextIndexFieldWordVo {
    private Long id;
    private Long wordId;
    private String word;
    private String targetField;
    private String targetType;
    private Long targetId;
    private int counter = 0;
    private Set<FullTextIndexOffsetVo> offsetList = new HashSet<>();

    public FullTextIndexFieldWordVo() {

    }

    public FullTextIndexFieldWordVo(Long _targetId, String _targetType, String _targetField, String _word) {
        this.targetId = _targetId;
        this.targetField = _targetField;
        this.targetType = _targetType;
        this.word = _word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        if (StringUtils.isNotBlank(word)) {
            word = word.trim();
        }
        this.word = word;
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public FullTextIndexFieldWordVo addCount() {
        this.counter += 1;
        return this;
    }

    public Set<FullTextIndexOffsetVo> getOffsetList() {
        return offsetList;
    }

    public void setOffsetList(Set<FullTextIndexOffsetVo> offsetList) {
        this.offsetList = offsetList;
    }

    public FullTextIndexFieldWordVo addOffset(FullTextIndexOffsetVo fullTextIndexOffsetVo) {
        if (fullTextIndexOffsetVo != null) {
            fullTextIndexOffsetVo.setFieldId(this.getId());
            offsetList.add(fullTextIndexOffsetVo);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullTextIndexFieldWordVo that = (FullTextIndexFieldWordVo) o;
        return word.equals(that.word) && targetField.equals(that.targetField) && targetType.equals(that.targetType) && targetId.equals(that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, targetField, targetType, targetId);
    }
}
