/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
