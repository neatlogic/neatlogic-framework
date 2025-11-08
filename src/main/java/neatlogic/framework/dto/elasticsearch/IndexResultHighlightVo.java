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

package neatlogic.framework.dto.elasticsearch;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class IndexResultHighlightVo implements Serializable {
    private String id;
    private Map<String, List<String>> highlightMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, List<String>> getHighlightMap() {
        return highlightMap;
    }

    public void setHighlightMap(Map<String, List<String>> highlightMap) {
        this.highlightMap = highlightMap;
    }
}
