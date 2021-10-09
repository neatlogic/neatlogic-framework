/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dto.fulltextindex;

import codedriver.framework.util.SnowflakeUtil;

import java.io.Serializable;
import java.util.Objects;

public class FullTextIndexWordVo implements Serializable {
    private Long id;
    private String word;
    private String type;

    public FullTextIndexWordVo() {

    }

    public FullTextIndexWordVo(String _word, String _type) {
        word = _word;
        type = _type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullTextIndexWordVo that = (FullTextIndexWordVo) o;
        return word.equals(that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}
