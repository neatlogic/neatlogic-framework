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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cluster {
    private final List<Node> nodeList;
    private final Map<String, String> propMap = new HashMap<>();
    private final String id;

    public Cluster(Builder builder) {
        this.nodeList = builder.nodeList;
        this.id = builder.id;
        propMap.put("labeljust", "l");
        if (StringUtils.isNotBlank(builder.label)) {
            propMap.put("label", builder.label);
        }
        if (StringUtils.isNotBlank(builder.style)) {
            propMap.put("style", builder.style);
        }
        if (StringUtils.isNotBlank(builder.label)) {
            propMap.put("label", builder.label);
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        if (CollectionUtils.isNotEmpty(nodeList) && nodeList.size() > 1) {
            str = new StringBuilder("subgraph " + this.id + " {");
            for (String key : propMap.keySet()) {
                str.append(key).append("=\"").append(propMap.get(key)).append("\";\n");
            }
            str.append(nodeList.stream().map(n -> "\"" + n.getId() + "\"").collect(Collectors.joining(";\n")));
            str.append("}");
        }
        return str.toString();
    }

    public static class Builder {
        private final List<Node> nodeList = new ArrayList<>();

        private final String id;
        private String label;
        private String style;

        public Builder(String _id) {
            this.id = _id;
        }

        public Cluster.Builder withLabel(String _label) {
            this.label = _label;
            return this;
        }


        public Cluster.Builder withStyle(String _style) {
            this.style = _style;
            return this;
        }

        public Cluster.Builder addNode(Node node) {
            if (node != null) {
                if (!nodeList.contains(node)) {
                    this.nodeList.add(node);
                }
            }
            return this;
        }

        public Cluster build() {
            return new Cluster(this);
        }

    }


}
