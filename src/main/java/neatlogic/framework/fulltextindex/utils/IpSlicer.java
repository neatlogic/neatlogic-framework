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

package neatlogic.framework.fulltextindex.utils;

import neatlogic.framework.fulltextindex.core.IFullTextSlicer;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexWordOffsetVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpSlicer implements IFullTextSlicer {

    private static List<FullTextIndexWordOffsetVo> extractIP(String content) {
        List<FullTextIndexWordOffsetVo> ipParts = new ArrayList<>();
        String ipPattern = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String ip = matcher.group();
            int start = matcher.start();

            // 生成各部分IP段
            String[] segments = ip.split("\\.");
            String part1 = segments[0] + "." + segments[1];
            String part2 = segments[0] + "." + segments[1] + "." + segments[2];

            // 添加到结果列表
            ipParts.add(new FullTextIndexWordOffsetVo(part1, "LETTER", start, start + part1.length()));
            ipParts.add(new FullTextIndexWordOffsetVo(part2, "LETTER", start, start + part2.length()));
        }

        return ipParts;
    }

    private static List<String> extractIPString(String keyword) {
        List<String> ipParts = new ArrayList<>();
        String ipPattern = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(keyword);

        while (matcher.find()) {
            String ip = matcher.group();

            // 生成各部分IP段
            String[] segments = ip.split("\\.");
            String part1 = segments[0] + "." + segments[1];
            String part2 = segments[0] + "." + segments[1] + "." + segments[2];
            //String part3 = segments[0] + "." + segments[1] + "." + segments[2] + "." + segments[3];

            // 添加到结果列表
            ipParts.add(part1);
            ipParts.add(part2);
            //ipParts.add(part3);
        }

        return ipParts;
    }


    @Override
    public String getType() {
        return "IP";
    }


    @Override
    public void sliceWord(List<FullTextIndexWordOffsetVo> wordList, String content) {
        List<FullTextIndexWordOffsetVo> ipParts = extractIP(content);
        if (CollectionUtils.isNotEmpty(ipParts)) {
            for (FullTextIndexWordOffsetVo ipPart : ipParts) {
                if (!wordList.contains(ipPart)) {
                    wordList.add(ipPart);
                }
            }
        }
    }

    @Override
    public void sliceKeyword(List<String> wordList, String keyword) {
        List<String> ipParts = extractIPString(keyword);
        if (CollectionUtils.isNotEmpty(ipParts)) {
            for (String ipPart : ipParts) {
                if (!wordList.contains(ipPart)) {
                    wordList.add(ipPart);
                }
            }
        }
    }
}
