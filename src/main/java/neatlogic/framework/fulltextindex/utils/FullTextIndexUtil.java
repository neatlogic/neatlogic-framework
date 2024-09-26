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

import neatlogic.framework.fulltextindex.core.FullTextSlicerFactory;
import neatlogic.framework.fulltextindex.core.IFullTextSlicer;
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
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullTextIndexUtil {
    static Logger logger = LoggerFactory.getLogger(FullTextIndexUtil.class);
    private static final Analyzer smartAnalyzer = new IKAnalyzer(true);//分词细一点
    private static final Analyzer termAnalyzer = new IKAnalyzer(true);//分词粗一点

    static {
        //初始化字典，便于后面动态增加词
        Dictionary.initial(DefaultConfig.getInstance());
    }

    private static final Pattern pattern = Pattern.compile("\"([^\"]+?)\"", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    //添加词进字典
    public static void addWord(String... words) {
        if (words != null && words.length > 0) {
            List<String> wordList = Arrays.asList(words);
            wordList.removeIf(StringUtils::isBlank);
            if (CollectionUtils.isNotEmpty(wordList)) {
                Set<String> newWordList = new HashSet<>();
                for (String word : wordList) {
                    word = word.replace("_", "");
                    word = word.replace("-", "");
                    String[] newwords = word.split("\\s+");
                    for (String w : newwords) {
                        if (StringUtils.isNotBlank(w) && StringUtils.isNotBlank(w.trim())) {
                            if (w.trim().length() <= 200) {
                                newWordList.add(w.trim());
                            }
                        }
                    }
                }
                Dictionary dictionary = Dictionary.getSingleton();
                dictionary.addWords(newWordList);
            }
        }
    }

    //添加词进字典
    public static void addWord(List<String> wordList) {
        if (CollectionUtils.isNotEmpty(wordList)) {
            wordList.removeIf(StringUtils::isBlank);
            if (CollectionUtils.isNotEmpty(wordList)) {
                Set<String> newWordList = new HashSet<>();
                for (String word : wordList) {
                    word = word.replace("_", "");
                    word = word.replace("-", "");
                    String[] words = word.split("\\s+");
                    for (String w : words) {
                        if (StringUtils.isNotBlank(w) && StringUtils.isNotBlank(w.trim())) {
                            if (w.trim().length() <= 200) {
                                newWordList.add(w.trim());
                            }
                        }
                    }
                }
                Dictionary dictionary = Dictionary.getSingleton();
                dictionary.addWords(newWordList);
            }
        }
    }

    /**
     * 对搜索关键字进行分词
     *
     * @param keyword 搜索关键字
     * @return 分词结果
     */
    public static List<String> sliceKeyword(String keyword) {
        List<String> wordList = new ArrayList<>();


        if (StringUtils.isNotBlank(keyword)) {
            //碰到-或_，统一去掉，当成一个词处理
            keyword = keyword.replace("_", "");
            keyword = keyword.replace("-", "");

            //转换所有双引号为半角双引号
            keyword = keyword.replace("'", "\"");
            keyword = keyword.replace("“", "\"");
            keyword = keyword.replace("”", "\"");

            if (keyword.contains("\"")) {
                StringBuffer temp = new StringBuffer();
                Matcher matcher = pattern.matcher(keyword);
                while (matcher.find()) {
                    String w = matcher.group(1);
                    if (!wordList.contains(w)) {
                        wordList.add(w);
                    }
                    matcher.appendReplacement(temp, " ");
                }
                matcher.appendTail(temp);
                keyword = temp.toString();
            }
            if (StringUtils.isNotBlank(keyword)) {
                try {
                    Reader reader = new StringReader(keyword);
                    TokenStream stream = smartAnalyzer.tokenStream(null, reader);
                    CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
                    stream.reset();
                    while (stream.incrementToken()) {
                        String w = term.toString();
                        if (!wordList.contains(w)) {
                            wordList.add(w);
                        }
                    }
                    stream.end();
                    stream.close();
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
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
            //碰到-或_，统一去掉，当成一个词处理
            content = content.replace("_", "");
            content = content.replace("-", "");

            Reader smartReader = new StringReader(content);
            //先用智能分词处理一次
            TokenStream smartStream = smartAnalyzer.tokenStream("", smartReader);
            CharTermAttribute smartTerm = smartStream.addAttribute(CharTermAttribute.class);
            OffsetAttribute smartOffset = smartStream.addAttribute(OffsetAttribute.class);// 位置数据
            TypeAttribute smartType = smartStream.addAttribute(TypeAttribute.class);
            smartStream.reset();
            while (smartStream.incrementToken()) {
                wordList.add(new FullTextIndexWordOffsetVo(smartTerm.toString(), smartType.type(), smartOffset.startOffset(), smartOffset.endOffset()));
            }

            smartStream.end();
            smartStream.close();

            //再用词组分词处理一次
            Reader termReader = new StringReader(content);
            TokenStream termStream = termAnalyzer.tokenStream("", termReader);
            CharTermAttribute termTerm = termStream.addAttribute(CharTermAttribute.class);
            OffsetAttribute termOffset = termStream.addAttribute(OffsetAttribute.class);// 位置数据
            TypeAttribute termType = termStream.addAttribute(TypeAttribute.class);
            termStream.reset();
            while (termStream.incrementToken()) {
                FullTextIndexWordOffsetVo wordOffsetVo = new FullTextIndexWordOffsetVo(termTerm.toString(), termType.type(), termOffset.startOffset(), termOffset.endOffset());
                if (!wordList.contains(wordOffsetVo)) {
                    wordList.add(wordOffsetVo);
                }
            }
            termStream.end();
            termStream.close();

            //额外的分词器
            List<IFullTextSlicer> slicerList = FullTextSlicerFactory.getSlicerList();
            if (CollectionUtils.isNotEmpty(slicerList)) {
                for (IFullTextSlicer slicer : slicerList) {
                    slicer.sliceWord(wordList, content);
                }
            }
        }
        return wordList;
    }

    public static void main(String[] arg) throws IOException, NoSuchFieldException, IllegalAccessException {
        Dictionary dictionary = Dictionary.getSingleton();
        dictionary.addWords(new ArrayList<String>() {{
            this.add("OBS华为云");
        }});

        String content = "OBS华为云";
        List<FullTextIndexWordOffsetVo> list = sliceWord(content);
        for (FullTextIndexWordOffsetVo vo : list) {
            System.out.println("s:" + vo.getStart() + " e:" + vo.getEnd() + " w:" + vo.getWord() + " t:" + vo.getType());
        }


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
