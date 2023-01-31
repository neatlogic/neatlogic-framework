/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.fulltextindex.dto.fulltextindex;

import java.util.Locale;

public class FullTextIndexWordOffsetVo {
    private String word;
    private String type;
    private int start;
    private int end;

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
