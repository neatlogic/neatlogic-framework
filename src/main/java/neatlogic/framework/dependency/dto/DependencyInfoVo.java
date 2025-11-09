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

package neatlogic.framework.dependency.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.exception.util.FreemarkerTransformException;
import neatlogic.framework.util.FreemarkerUtil;
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
                path = "<span>" +
                        lastName +
                        "</span>" +
                        "<span class='text-tip'>" +
                        " [" +
                        groupName +
                        " / " +
                        String.join(" / ", pathList) +
                        "]" +
                        "</span>";
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
