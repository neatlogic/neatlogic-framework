/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util.pdf;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
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
        List<Chunk> chunks = title.getChunks();
        if (CollectionUtils.isNotEmpty(chunks)) {
            for (Chunk chunk : chunks) {
                Map<String, Object> attributes = chunk.getAttributes();
                if (MapUtils.isEmpty(attributes)) {
                    continue;
                }
                String localDestination = (String) attributes.get(Chunk.LOCALDESTINATION);
                if (localDestination == null) {
                    continue;
                }
                localDestinationMap.put(title.getContent(), localDestination);
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
