/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
}
