/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dto.fulltextindex;

import codedriver.framework.fulltextindex.utils.FullTextIndexUtil;
import codedriver.framework.util.HtmlUtil;
import codedriver.framework.util.Md5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class FullTextIndexVo {
    private final static Logger logger = LoggerFactory.getLogger(FullTextIndexVo.class);
    private Long targetId;
    private String targetType;
    private String targetField;
    private List<FullTextIndexWordOffsetVo> wordOffsetVoList;
    private final Map<String, WordVo> fieldContentMap = new HashMap<>();
    private Map<String, List<FullTextIndexWordOffsetVo>> wordOffsetMap = new HashMap<>();

    public FullTextIndexVo() {
    }

    public FullTextIndexVo(Long _targetId, String _targetType) {
        targetId = _targetId;
        targetType = _targetType;
    }

    public Long getTargetId() {
        return targetId;
    }


    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public void addFieldContent(String field, WordVo wordVo) {
        if (StringUtils.isNotBlank(field) && StringUtils.isNotBlank(wordVo.getContent())) {
            wordVo.setContent(HtmlUtil.removeHtml(wordVo.getContent().trim()));
            fieldContentMap.put(field.trim().toLowerCase(Locale.ROOT), wordVo);
        }
    }

    public void addFieldContentWithHtml(String field, WordVo wordVo) {
        if (StringUtils.isNotBlank(field) && StringUtils.isNotBlank(wordVo.getContent())) {
            fieldContentMap.put(field.trim().toLowerCase(Locale.ROOT), wordVo);
        }
    }

    public List<FullTextIndexContentVo> getContentList() {
        List<FullTextIndexContentVo> contentList = new ArrayList<>();
        for (String field : fieldContentMap.keySet()) {
            FullTextIndexContentVo contentVo = new FullTextIndexContentVo();
            contentVo.setTargetId(this.getTargetId());
            contentVo.setTargetType(this.getTargetType());
            contentVo.setTargetField(field);
            contentVo.setContent(fieldContentMap.get(field).getContent());
            contentList.add(contentVo);
        }
        return contentList;
    }

    public List<FullTextIndexWordVo> getWordList() {
        Set<FullTextIndexWordVo> wordList = new HashSet<>();
        if (MapUtils.isNotEmpty(fieldContentMap)) {
            for (String field : fieldContentMap.keySet()) {
                try {
                    if (!wordOffsetMap.containsKey(field)) {
                        if(fieldContentMap.get(field).getNeedSliceWord()) {
                            wordOffsetMap.put(field, FullTextIndexUtil.sliceWord(fieldContentMap.get(field).getContent()));
                        }else{
                            List<FullTextIndexWordOffsetVo> wordTmpList = new ArrayList<>();
                            wordTmpList.add(new FullTextIndexWordOffsetVo(Md5Util.encryptBASE64(fieldContentMap.get(field).getContent()), "MD5", 0, fieldContentMap.get(field).getContent().length()-1));
                            wordOffsetMap.put(field, wordTmpList);
                        }
                    }
                    List<FullTextIndexWordOffsetVo> wordOffsetList = wordOffsetMap.get(field);
                    if (CollectionUtils.isNotEmpty(wordOffsetList)) {
                        for (FullTextIndexWordOffsetVo wordOffsetVo : wordOffsetList) {
                            wordList.add(new FullTextIndexWordVo(wordOffsetVo.getWord(), wordOffsetVo.getType()));
                        }
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return new ArrayList<>(wordList);
    }


    public List<FullTextIndexFieldWordVo> getFieldList() {
        Map<String, FullTextIndexFieldWordVo> fieldCountMap = new HashMap<>();
        if (MapUtils.isNotEmpty(fieldContentMap)) {
            for (String field : fieldContentMap.keySet()) {
                try {
                    if (!wordOffsetMap.containsKey(field)) {
                        if(fieldContentMap.get(field).getNeedSliceWord()) {
                            wordOffsetMap.put(field, FullTextIndexUtil.sliceWord(fieldContentMap.get(field).getContent()));
                        }else{
                            List<FullTextIndexWordOffsetVo> wordTmpList = new ArrayList<>();
                            wordTmpList.add(new FullTextIndexWordOffsetVo(Md5Util.encryptBASE64(fieldContentMap.get(field).getContent()), "MD5", 0, fieldContentMap.get(field).getContent().length()-1));
                            wordOffsetMap.put(field, wordTmpList);
                        }
                    }
                    List<FullTextIndexWordOffsetVo> wordOffsetList = wordOffsetMap.get(field);
                    if (CollectionUtils.isNotEmpty(wordOffsetList)) {
                        for (FullTextIndexWordOffsetVo wordOffsetVo : wordOffsetList) {
                            if (!fieldCountMap.containsKey(field + "#" + wordOffsetVo.getWord())) {
                                fieldCountMap.put(field + "#" + wordOffsetVo.getWord(), new FullTextIndexFieldWordVo(this.getTargetId(), this.getTargetType(), field, wordOffsetVo.getWord()));
                            }
                            fieldCountMap.get(field + "#" + wordOffsetVo.getWord()).addCount().addOffset(new FullTextIndexOffsetVo(wordOffsetVo.getStart(), wordOffsetVo.getEnd()));
                        }
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return new ArrayList<>(fieldCountMap.values());

    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }


    public Map<String, List<FullTextIndexWordOffsetVo>> getWordOffsetMap() {
        return wordOffsetMap;
    }

    public void setWordOffsetMap(Map<String, List<FullTextIndexWordOffsetVo>> wordOffsetMap) {
        this.wordOffsetMap = wordOffsetMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullTextIndexVo that = (FullTextIndexVo) o;
        return targetId.equals(that.targetId) && targetType.equals(that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, targetType);
    }

    public List<FullTextIndexWordOffsetVo> getWordOffsetVoList() {
        return wordOffsetVoList;
    }

    public void setWordOffsetVoList(List<FullTextIndexWordOffsetVo> wordOffsetVoList) {
        this.wordOffsetVoList = wordOffsetVoList;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public Map<String, WordVo> getFieldContentMap() {
        return fieldContentMap;
    }

    public static class WordVo{
        private Boolean isNeedSliceWord = true;
        private String content;

        public WordVo(){

        }
        public WordVo(String content) {
            this.content = content;
        }

        public WordVo(Boolean isNeedSliceWord, String content) {
            this.isNeedSliceWord = isNeedSliceWord;
            this.content = content;
        }

        public Boolean getNeedSliceWord() {
            return isNeedSliceWord;
        }

        public void setNeedSliceWord(Boolean needSliceWord) {
            isNeedSliceWord = needSliceWord;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
