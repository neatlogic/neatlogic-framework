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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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
