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

package neatlogic.framework.graphviz;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Layer {
    private final List<Node> nodeList;
    private final String id;
    private final Map<String, String> propMap = new HashMap<>();

    private Layer(Builder builder) {
        nodeList = builder.nodeList;
        if (CollectionUtils.isNotEmpty(nodeList)) {
            for (Node node : nodeList) {
                node.setLayer(this);
            }
        }
        this.id = builder.id;
        propMap.put("id", builder.id);
        if (builder.label != null) {
            propMap.put("label", builder.label);
        }
        if (StringUtils.isNotBlank(builder.className)) {
            propMap.put("class", builder.className);
        }
        if (StringUtils.isNotBlank(builder.tooltip)) {
            propMap.put("tooltip", builder.tooltip);
        }
        if (StringUtils.isNotBlank(builder.fontcolor)) {
            propMap.put("fontcolor", builder.fontcolor);
        }
        if (StringUtils.isNotBlank(builder.fontName)) {
            propMap.put("fontname", builder.fontName);
        }
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        Iterator<String> itKey = propMap.keySet().iterator();
        String propString = "";
        while (itKey.hasNext()) {
            String key = itKey.next();
            if (StringUtils.isNotBlank(propString)) {
                propString += ",";
            }
            propString += key + "=\"" + propMap.get(key) + "\"";
        }
        String str = "rank=same;\n";
        str += "\"" + this.id + "\"[shape=\"none\"," + propString + "];\n";
        if (CollectionUtils.isNotEmpty(nodeList)) {
            str += nodeList.stream().map(n -> n.toString().trim()).collect(Collectors.joining(";\n"));
        }
        return str;
    }

    public static class Builder {
        private final List<Node> nodeList = new ArrayList<>();
        private final String id;
        private String label;
        private String className = "layer";
        private String tooltip;
        private String fontcolor;

        private String fontName = "Times";

        public Builder withFontName(String _fontname) {
            this.fontName = _fontname;
            return this;
        }

        public Builder withFontColor(String _fontcolor) {
            this.fontcolor = _fontcolor;
            return this;
        }

        public Builder(String id) {
            this.id = id;
        }

        public Builder withLabel(String _label) {
            this.label = _label;
            return this;
        }

        public Builder withClass(String _class) {
            this.className = _class;
            return this;
        }

        public Builder withTooltip(String _tooltip) {
            this.tooltip = _tooltip;
            return this;
        }

        public Builder addNode(Node node) {
            if (node != null) {
                this.nodeList.add(node);
            }
            return this;
        }

        public Layer build() {
            return new Layer(this);
        }


    }
}
