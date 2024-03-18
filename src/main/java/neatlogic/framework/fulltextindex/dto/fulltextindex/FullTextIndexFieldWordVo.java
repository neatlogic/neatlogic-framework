/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.fulltextindex.dto.fulltextindex;

import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
