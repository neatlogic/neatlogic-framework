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

package neatlogic.framework.graphviz;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Link {
    private final String from;
    private final String to;
    private final Map<String, Object> propMap = new HashMap<>();

    private Link(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        if (StringUtils.isNotBlank(builder.tailLabel)) {
            propMap.put("taillabel", builder.tailLabel);
        }
        if (StringUtils.isNotBlank(builder.headLabel)) {
            propMap.put("headlabel", builder.headLabel);
        }
        if (StringUtils.isNotBlank(builder.label)) {
            propMap.put("label", builder.label);
        }
        if (builder.labelDistance != null) {
            propMap.put("labeldistance", builder.labelDistance);
        }
        if (StringUtils.isNotBlank(builder.fontcolor)) {
            propMap.put("fontcolor", builder.fontcolor);
        }
        if (builder.fontsize != null) {
            propMap.put("fontsize", builder.fontsize);
        }
        if (builder.style != null) {
            propMap.put("style", builder.style);
        }
        if (builder.fontname != null) {
            propMap.put("fontname", builder.fontname);
        }
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
        String str = "\"" + this.from + "\"->\"" + this.to + "\"";
        if (StringUtils.isNotBlank(propString)) {
            str += "[" + propString + ",labelangle=\"180\"]";
        }
        return str;
    }

    public static class Builder {
        private final String from;
        private final String to;
        private String tailLabel;
        private String headLabel;
        private String label;
        private Integer labelDistance;
        private Double fontsize;
        private String fontcolor;
        private String style;

        private String fontname = "Times";

        public Builder(String _from, String _to) {
            this.from = _from;
            this.to = _to;
        }

        public Builder withFontName(String _fontname) {
            this.fontname = _fontname;
            return this;
        }

        public Builder withHeadLabel(String _headLabel) {
            this.headLabel = _headLabel;
            return this;
        }

        public Builder withTailLabel(String _tailLabel) {
            this.tailLabel = _tailLabel;
            return this;
        }

        public Builder withLabel(String _label) {
            this.label = _label;
            return this;
        }

        public Builder withLabelDistance(int _labelDistance) {
            this.labelDistance = _labelDistance;
            return this;
        }

        public Builder setFontSize(double _fontsize) {
            this.fontsize = _fontsize;
            return this;
        }

        public Builder setFontColor(String _fontcolor) {
            this.fontcolor = _fontcolor;
            return this;
        }

        public Builder setStyle(String _style) {
            this.style = _style;
            return this;
        }

        public Link build() {
            return new Link(this);
        }
    }
}
