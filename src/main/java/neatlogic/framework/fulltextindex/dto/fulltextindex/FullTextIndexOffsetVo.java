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

import java.util.Objects;

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
