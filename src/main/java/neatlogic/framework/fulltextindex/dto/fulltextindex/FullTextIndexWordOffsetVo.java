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

import java.util.Locale;
import java.util.Objects;

public class FullTextIndexWordOffsetVo {
    private String word;
    private String type;
    private int start;
    private int end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullTextIndexWordOffsetVo)) return false;
        FullTextIndexWordOffsetVo that = (FullTextIndexWordOffsetVo) o;
        return start == that.start && end == that.end && Objects.equals(word, that.word) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, type, start, end);
    }

    public FullTextIndexWordOffsetVo() {
    }

    public FullTextIndexWordOffsetVo(String _word, String _type, int _start, int _end) {
        word = _word.toLowerCase(Locale.ROOT);
        type = _type;
        start = _start;
        end = _end;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
