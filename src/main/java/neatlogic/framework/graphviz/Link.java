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

package neatlogic.framework.graphviz;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Link {
    private final String from;
    private final String to;
    private final Map<String, Object> propMap = new HashMap<>();

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

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
