/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dto.fulltextindex;

import java.util.HashSet;
import java.util.Set;

public class FullTextIndexTargetVo {
    private Long targetId;
    private Set<FullTextIndexWordVo> wordList;
    private Set<FullTextIndexFieldWordVo> fieldList;


    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Set<FullTextIndexWordVo> getWordList() {
        return wordList;
    }

    public void addWord(FullTextIndexWordVo wordVo) {
        if (wordList == null) {
            wordList = new HashSet<>();
        }
        wordList.add(wordVo);
    }

    public void adField(FullTextIndexFieldWordVo fieldVo) {
        if (fieldList == null) {
            fieldList = new HashSet<>();
        }
        fieldList.add(fieldVo);
    }

    public void setWordList(Set<FullTextIndexWordVo> wordList) {
        this.wordList = wordList;
    }

    public Set<FullTextIndexFieldWordVo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(Set<FullTextIndexFieldWordVo> fieldList) {
        this.fieldList = fieldList;
    }
}
