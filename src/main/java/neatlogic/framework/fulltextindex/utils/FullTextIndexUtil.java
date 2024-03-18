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

package neatlogic.framework.fulltextindex.utils;

import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordOffsetVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullTextIndexUtil {
    static Logger logger = LoggerFactory.getLogger(FullTextIndexUtil.class);
    private static final Analyzer indexAnalyzer = new IKAnalyzer(false);//分词细一点
    private static final Analyzer searchAnalyzer = new IKAnalyzer(true);//分词粗一点
    private static final Pattern pattern = Pattern.compile("\"([^\"]+?)\"", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /**
     * 对搜索关键字进行分词
     *
     * @param keyword 搜索关键字
     * @return 分词结果
     */
    public static Set<String> sliceKeyword(String keyword) {
        Set<String> wordList = new HashSet<>();
        /*
         * for (String w : word.split("[\\s]+")) { if (StringUtils.isNotBlank(w)
         * && !wholdWordList.contains(w)) { wholdWordList.add(w); } }
         */
        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.replace("'", "\"");
            keyword = keyword.replace("“", "\"");
            keyword = keyword.replace("”", "\"");

            if (keyword.contains("\"")) {
                StringBuffer temp = new StringBuffer();
                Matcher matcher = pattern.matcher(keyword);
                while (matcher.find()) {
                    String w = matcher.group(1);
                    wordList.add(w);
                    matcher.appendReplacement(temp, " ");
                }
                matcher.appendTail(temp);
                keyword = temp.toString();
            }
            if (StringUtils.isNotBlank(keyword)) {
                try {
                    Reader reader = new StringReader(keyword);
                    TokenStream stream = searchAnalyzer.tokenStream(null, reader);
                    CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
                    stream.reset();
                    while (stream.incrementToken()) {
                        String w = term.toString();
                        wordList.add(w);
                    }
                    stream.end();
                    stream.close();
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
        if (CollectionUtils.isEmpty(wordList) && StringUtils.isNotBlank(keyword)) {
            wordList.add(keyword);
        }
        return wordList;
    }

    /**
     * 对原始文本进行分词，用于创建索引
     *
     * @param content 原始文本
     * @return 分词结果
     * @throws IOException 异常
     */
    public static List<FullTextIndexWordOffsetVo> sliceWord(String content) throws IOException {
        List<FullTextIndexWordOffsetVo> wordList = new ArrayList<>();
        if (StringUtils.isNotBlank(content)) {
            Reader reader = new StringReader(content);
            TokenStream stream = indexAnalyzer.tokenStream("", reader);
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);// 位置数据
            TypeAttribute type = stream.addAttribute(TypeAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                wordList.add(new FullTextIndexWordOffsetVo(term.toString(), type.type(), offset.startOffset(), offset.endOffset()));
            }
            stream.end();
            stream.close();
        }
        return wordList;
    }

    /**
     * @Description: 根据最匹配的关键字截取前后maxWord 长度的内容
     * @Author: 89770
     * @Date: 2021/3/1 18:35
     * @Params: [keywordStartIndex, maxWord, contentParam]
     * @Returns: java.lang.String
     **/
    public static String getShortcut(int keywordStartIndex, int keywordEndIndex, int maxWord, String contentParam) {
        String content = StringUtils.EMPTY;
        //计算截取下标
        int startIndex = 0;
        int endIndex = 0;
        //先找到截取起始点，
        startIndex = Math.max(0, keywordStartIndex - maxWord);
        if (StringUtils.isNotBlank(contentParam)) {
            int contentLen = contentParam.length();
            //找到截取结束点
            endIndex = Math.min(contentLen, keywordEndIndex + maxWord);
            //截取长度
            content = contentParam.substring(startIndex, endIndex);
            if (startIndex > 0) {
                content = "..." + content;
            }
            if (endIndex < contentParam.length()) {
                content = content + "...";
            }
        }
        return content;
    }
}
