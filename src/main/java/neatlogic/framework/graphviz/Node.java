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

public class Node {
    private final Map<String, String> propMap;
    private final String id;
    private List<String> className;
    private Layer layer;

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    private Node(Builder builder) {
        this.id = builder.id;
        propMap = new HashMap<>();
        propMap.put("id", builder.id);
        if (builder.label != null) {
            propMap.put("label", builder.label);
        }
        if (StringUtils.isNotBlank(builder.tooltip)) {
            propMap.put("tooltip", builder.tooltip);
        }
        if (CollectionUtils.isNotEmpty(builder.className)) {
            propMap.put("class", String.join(" ", builder.className));
            this.className = builder.className;
        }
        if (StringUtils.isNotBlank(builder.fontcolor)) {
            propMap.put("fontcolor", builder.fontcolor);
        }
        if (StringUtils.isNotBlank(builder.image)) {
            propMap.put("image", builder.image);
        }
        if (StringUtils.isNotBlank(builder.fontname)) {
            propMap.put("fontname", builder.fontname);
        }
        if (StringUtils.isNotBlank(builder.shape)) {
            propMap.put("shape", builder.shape);
        } else {
            propMap.put("shape", "none");
        }
        if (StringUtils.isNotBlank(builder.style)) {
            propMap.put("style", builder.style);
        }
        if (StringUtils.isNotBlank(builder.margin)) {
            propMap.put("margin", builder.margin);
        }
        if (StringUtils.isNotBlank(builder.labelloc)) {
            propMap.put("labelloc", builder.labelloc);
        }
        if (StringUtils.isNotBlank(builder.height)) {
            propMap.put("height", builder.height);
        }
        if (StringUtils.isNotBlank(builder.width)) {
            propMap.put("width", builder.width);
        }
    }

    public String getId() {
        return id;
    }

    public List<String> getClassName() {
        return className;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public String toString() {
        Iterator<String> itKey = propMap.keySet().iterator();
        String propString = "";
        while (itKey.hasNext()) {
            String key = itKey.next();
            if (StringUtils.isNotBlank(propString)) {
                propString += ",";
            }
            String value = propMap.get(key);
            value = value.replace("\"", "\\\"");
            propString += key + "=" + (value.startsWith("<") ? "" : "\"") + value
                    + (value.startsWith("<") ? "" : "\"");
        }
        // lobelloc控制label位置
        return "\"" + this.id + "\"[" + propString + ",imagepos=\"tc\"]";
    }

    public static class Builder {
        private String id;
        private String label;
        private String tooltip;
        private List<String> className = new ArrayList<>();
        private String fontcolor;
        private String image;
        private String icon;
        private String style;
        private String shape;
        private String margin;
        private String height;
        private String width;
        private String fontname = "Times";
        private String labelloc = "b";


        public Builder(String _id) {
            this.id = _id;
        }

        public Builder withLabelloc(String _labelloc) {
            this.labelloc = _labelloc;
            return this;
        }

        public Builder withHeight(String _height) {
            this.height = _height;
            return this;
        }

        public Builder withWidth(String _width) {
            this.width = _width;
            return this;
        }

        public Builder withMargin(String _margin) {
            this.margin = _margin;
            return this;
        }

        public Builder withStyle(String _style) {
            this.style = _style;
            return this;
        }

        public Builder withIcon(String _icon) {
            this.icon = _icon;
            return this;
        }

        public Builder withShape(String _shape) {
            this.shape = _shape;
            return this;
        }


        public Builder withLabel(String _label) {
            if (_label != null) {
                this.label = _label.trim();
            }
            return this;
        }

        public Builder withTooltip(String _tooltip) {
            this.tooltip = _tooltip;
            return this;
        }

        public Builder addClass(String _class) {
            if (!this.className.contains(_class)) {
                this.className.add(_class);
            }
            return this;
        }

        public Builder withFontColor(String _fontcolor) {
            this.fontcolor = _fontcolor;
            return this;
        }

        public Builder withFontName(String _fontname) {
            this.fontname = _fontname;
            return this;
        }

        public Builder withImage(String _image) {
            this.image = "/resource/img/icons/" + _image + ".png";
            return this;
        }

        public Node build() {
            return new Node(this);
        }
    }
}
