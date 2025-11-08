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

package neatlogic.framework.util.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录章节与页码关系、章节的锚点，用于生成目录
 */
public class CharterInfoEvent extends PdfPageEventHelper {

    Map<String, Integer> index = new LinkedHashMap<>();
    Map<String, String> localDestinationMap = new HashMap<>();
    Map<String, Integer> depthMap = new HashMap<>();

    @Override
    public void onChapter (PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        List<Element> chunks = title.getChunks();
        if (CollectionUtils.isNotEmpty(chunks)) {
            for (Element element : chunks) {
                if (element instanceof Chunk chunk) {
                    Map<String, Object> attributes = chunk.getChunkAttributes();
                    if (MapUtils.isEmpty(attributes)) {
                        continue;
                    }
                    String localDestination = (String) attributes.get(Chunk.LOCALDESTINATION);
                    if (localDestination == null) {
                        continue;
                    }
                    localDestinationMap.put(title.getContent(), localDestination);
                }
                break;
            }
        }
        index.put(title.getContent(), writer.getPageNumber());
    }

    @Override
    public void onSection (PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
        depthMap.put(title.getContent(), depth);
        onChapter(writer, document, paragraphPosition, title);
    }

    public Map<String, Integer> getIndex() {
        return index;
    }

    public String getLocalDestination(String key) {
        return localDestinationMap.get(key);
    }

    public Integer getDepth(String key) {
        return depthMap.get(key);
    }
}
