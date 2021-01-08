package codedriver.framework.fullindex.core;

import codedriver.framework.fullindex.dto.WordVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Title: FullIndexManager
 * @Package: codedriver.framework.fullindex.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/1/72:07 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class FullIndexUtil {
    private static final Analyzer indexAnalyzer = new IKAnalyzer(false);
    private static final Analyzer searchAnalyzer = new IKAnalyzer(true);
    private static final Pattern pattern = Pattern.compile("\"([^\"]+?)\"", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public static List<WordVo> sliceWord(String content) throws IOException {
        List<WordVo> wordList = new ArrayList<>();
        if (StringUtils.isNotBlank(content)) {
            Reader reader = new StringReader(content);
            TokenStream stream = indexAnalyzer.tokenStream("", reader);
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);// 位置数据
            TypeAttribute type = stream.addAttribute(TypeAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                wordList.add(new WordVo(term.toString(), type.type(), offset.startOffset(), offset.endOffset()));
            }
            stream.end();
            stream.close();
        }
        return wordList;
    }
}
