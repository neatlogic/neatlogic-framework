/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.dependency.dto;

import neatlogic.framework.exception.util.FreemarkerTransformException;
import neatlogic.framework.util.FreemarkerUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author linbq
 * @since 2022/1/11 12:16
 **/
public class DependencyInfoVo {
    private Object value;
    private String text;
    private String path;
    private String url;
    private JSONObject config;
    private final String groupName;

//    @JSONField(serialize = false)
//    private String pathFormat;
    @JSONField(serialize = false)
    private final String lastName;
    @JSONField(serialize = false)
    private final List<String> pathList;
    @JSONField(serialize = false)
    private final String urlFormat;

//    public DependencyInfoVo(Object value, JSONObject config, String pathFormat, String urlFormat, String groupName) {
//        this.value = value;
//        this.config = config;
//        this.pathFormat = pathFormat;
//        this.urlFormat = urlFormat;
//        this.groupName = groupName;
//    }

    public DependencyInfoVo(Object value, JSONObject config, String lastName, List<String> pathList, String urlFormat, String groupName) {
        this.value = value;
        this.config = config;
        this.lastName = lastName;
        this.pathList = pathList;
        this.urlFormat = urlFormat;
        this.groupName = groupName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getText() {
        if (text == null && StringUtils.isNotBlank(getPath()) && StringUtils.isNotBlank(getUrl())) {
            text = "<a href='" + url + "' target='_blank'>" + path + "</a>";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        if (path == null) {
            if (StringUtils.isNotBlank(lastName) && CollectionUtils.isNotEmpty(pathList)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<span>");
                stringBuilder.append(lastName);
                stringBuilder.append("</span>");
                stringBuilder.append("<span class='text-tip'>");
                stringBuilder.append(" [");
                stringBuilder.append(groupName);
                stringBuilder.append(" / ");
                stringBuilder.append(String.join(" / ", pathList));
                stringBuilder.append("]");
                stringBuilder.append("</span>");
                path = stringBuilder.toString();
            }
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        if (url == null && StringUtils.isNotBlank(urlFormat)) {
            if (MapUtils.isNotEmpty(config)) {
                try {
                    url = FreemarkerUtil.transform(config, urlFormat);
                } catch (FreemarkerTransformException e) {
                    url = urlFormat;
                }
            } else {
                url = urlFormat;
            }
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getUrlFormat() {
        return urlFormat;
    }
}
