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

import neatlogic.framework.graphviz.enums.LayoutType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Graphviz {
    List<Layer> layerList;
    List<Link> linkList;
    List<Cluster> clusterList;

    List<Node> nodeList;
    LayoutType layout;
    String rankdir;

    private Graphviz(Builder builder) {
        this.layerList = builder.layerList;
        this.linkList = builder.linkList;
        this.clusterList = builder.clusterList;
        this.layout = builder.layout;
        this.nodeList = builder.nodeList;
        this.rankdir = builder.rankdir;
    }

    public Layer getLayerById(String id) {
        if (CollectionUtils.isNotEmpty(layerList)) {
            return this.layerList.stream().filter(layer -> layer.getId().equals(id)).findFirst().orElse(null);
        }
        return null;
    }

    public void removeLayer(String id) {
        if (CollectionUtils.isNotEmpty(layerList)) {
            Layer layer = this.getLayerById(id);
            if (layer != null) {
                if (CollectionUtils.isNotEmpty(layer.getNodeList())) {
                    for (Node node : layer.getNodeList()) {
                        this.removeNode(node.getId());
                    }
                }
                layerList.remove(layer);
            }

        }
    }

    public void removeNode(String id) {
        if (CollectionUtils.isNotEmpty(linkList)) {
            this.linkList.removeIf(link -> link.getFrom().equals(id) || link.getTo().equals(id));
        }
        if (CollectionUtils.isNotEmpty(nodeList)) {
            this.nodeList.removeIf(node -> node.getId().equals(id));
        }
        if (CollectionUtils.isNotEmpty(layerList)) {
            for (Layer layer : layerList) {
                if (CollectionUtils.isNotEmpty(layer.getNodeList())) {
                    this.nodeList.removeIf(node -> node.getId().equals(id));
                }
            }
        }
    }

    public Node getNodeById(String id) {
        if (CollectionUtils.isNotEmpty(nodeList)) {
            return this.nodeList.stream().filter(node -> node.getId().equals(id)).findFirst().orElse(null);
        }
        return null;
    }

    public void addLink(Link link) {
        this.linkList.add(link);
    }


    public List<Node> getNodeList() {
        List<Node> _nodeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(this.layerList)) {
            for (Layer layer : this.layerList) {
                if (CollectionUtils.isNotEmpty(layer.getNodeList())) {
                    _nodeList.addAll(layer.getNodeList());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(this.nodeList)) {
            _nodeList.addAll(nodeList);
        }
        return _nodeList;
    }

    public List<Link> getOutgoingLinkById(String id) {
        if (CollectionUtils.isNotEmpty(this.linkList)) {
            return this.linkList.stream().filter(link -> link.getFrom().equals(id)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<Link> getIncomingLinkById(String id) {
        if (CollectionUtils.isNotEmpty(this.linkList)) {
            return this.linkList.stream().filter(link -> link.getTo().equals(id)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("digraph {\n");
        str.append("layout=").append(this.layout.getValue()).append(";\n");
        str.append("overlap=false;\n");
        str.append("bgcolor=\"transparent\";\n");
        //str.append("Node [fontname=Arial, shape=\"ellipse\", fixedsize=\"true\", width=\"1.1\", height=\"1.1\", color=\"transparent\" ,fontsize=12];\n");
        //str.append("Edge [fontname=Arial, minlen=\"1\", color=\"#7f8fa6\", fontsize=10];\n");
        str.append("ranksep = 1.1;\n");
        str.append("nodesep=.7;\n");
        str.append("size = \"11,8\";\n");
        str.append("edge [arrowhead=\"vee\"];\n");
        str.append("rankdir=").append(this.rankdir).append(";");
        str.append("newrank=true;");//关键属性，分层和cluster同时生效
        if (layout.getSupportLayer()) {
            if (CollectionUtils.isNotEmpty(layerList)) {
                str.append(layerList.stream().map(d -> "{" + d.toString() + "}\n").collect(Collectors.joining("")));
                if (layerList.size() > 1) {
                    str.append("{node[];").append(layerList.stream().map(d -> "\"" + d.getId() + "\"").collect(Collectors.joining("->"))).append("[style=invis]}");
                }
            }
        } else {
            if (CollectionUtils.isNotEmpty(layerList)) {
                for (Layer layer : layerList) {
                    if (CollectionUtils.isNotEmpty(layer.getNodeList())) {
                        for (Node node : layer.getNodeList()) {
                            str.append(node.toString()).append(";\n");
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(nodeList)) {
            for (Node node : nodeList) {
                str.append(node.toString()).append(";\n");
            }
        }
        if (CollectionUtils.isNotEmpty(linkList)) {
            str.append(linkList.stream().map(Link::toString).collect(Collectors.joining(";\n")));
        }
        if (CollectionUtils.isNotEmpty(clusterList)) {
            str.append(clusterList.stream().map(Cluster::toString).collect(Collectors.joining("\n")));
        }
        str.append("}");
        return str.toString();
    }

    public static class Builder {
        List<Layer> layerList = new ArrayList<>();
        List<Link> linkList = new ArrayList<>();
        List<Cluster> clusterList = new ArrayList<>();

        List<Node> nodeList = new ArrayList<>();
        LayoutType layout;
        String rankdir = "TB";

        public Builder withRankdir(String rankdir) {
            this.rankdir = rankdir;
            return this;
        }

        public Builder(LayoutType layout) {
            this.layout = layout;
        }

        public Builder addLayer(Layer layer) {
            if (layer != null) {
                layerList.add(layer);
            }
            return this;
        }

        public Builder addNode(Node node) {
            if (node != null) {
                nodeList.add(node);
            }
            return this;
        }

        public Builder addLink(Link link) {
            if (link != null) {
                linkList.add(link);
            }
            return this;
        }

        public Builder addCluster(Cluster cluster) {
            if (cluster != null) {
                clusterList.add(cluster);
            }
            return this;
        }

        public Graphviz build() {
            return new Graphviz(this);
        }
    }

}
