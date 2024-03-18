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
