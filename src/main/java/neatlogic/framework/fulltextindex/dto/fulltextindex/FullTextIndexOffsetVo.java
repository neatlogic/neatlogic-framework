/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
