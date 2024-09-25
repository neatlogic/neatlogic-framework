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
