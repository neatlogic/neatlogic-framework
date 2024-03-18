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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Node {
    private final Map<String, String> propMap;
    private final String id;
    private final int segment;
    private final static int LABEL_LENGTH = 10;

    private Node(Builder builder) {
        this.id = builder.id;
        propMap = new HashMap<>();
        propMap.put("id", builder.id);
        if (StringUtils.isNotBlank(builder.label)) {
            propMap.put("label", builder.label);
        }
        if (StringUtils.isNotBlank(builder.tooltip)) {
            propMap.put("tooltip", builder.tooltip);
        }
        if (CollectionUtils.isNotEmpty(builder.className)) {
            propMap.put("class", String.join(" ", builder.className));
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
        this.segment = builder.segment;
    }

    public String getId() {
        return id;
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

    private int getSegment() {
        return segment;
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
            propString += key + "=" + (value.startsWith("<") ? "" : "\"") + propMap.get(key)
                    + (value.startsWith("<") ? "" : "\"");
        }
        // lobelloc控制label位置
        return "\"" + this.id + "\"[shape=\"none\"," + propString + ",labelloc=\"b\",imagepos=\"tc\",height="
                + (0.85 + 0.4 * segment) + "]";
    }

    public static class Builder {
        private String id;
        private String label;
        private String tooltip;
        private List<String> className = new ArrayList<>();
        private String fontcolor;
        private String image;
        private String icon;
        private int segment;
        private String fontname = "Times";


        public Builder(String _id) {
            this.id = _id;
            //className.add("cinode");
        }

        public Builder withIcon(String _icon) {
            this.icon = _icon;
            return this;
        }


        public Builder withLabel(String _label) {
            if (StringUtils.isNotBlank(_label)) {
                _label = _label.trim();
                /*List<String> labelList = new ArrayList<>();
                int s = _label.length() / LABEL_LENGTH;
                for (int i = 0; i < s; i++) {
                    labelList.add(_label.substring(0, Math.min(LABEL_LENGTH, _label.length())));
                    _label = _label.substring(Math.min(LABEL_LENGTH, _label.length()));
                }
                if (StringUtils.isNotBlank(_label)) {
                    labelList.add(_label);
                }
                StringBuilder returnLabel = new StringBuilder();
                segment = 0;
                for (String lb : labelList) {
                    if (StringUtils.isNotBlank(returnLabel)) {
                        returnLabel.append("\n");
                        segment++;
                    }
                    returnLabel.append(lb);
                }*/
                segment = 0;
                this.label = _label;//returnLabel.toString();
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
