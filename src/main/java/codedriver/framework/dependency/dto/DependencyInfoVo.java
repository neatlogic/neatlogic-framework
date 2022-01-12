/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.dto;

import codedriver.framework.exception.util.FreemarkerTransformException;
import codedriver.framework.util.FreemarkerUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

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

    @JSONField(serialize = false)
    String pathFormat;
    @JSONField(serialize = false)
    String urlFormat;

    public DependencyInfoVo(Object value, JSONObject config, String pathFormat, String urlFormat) {
        this.value = value;
        this.config = config;
        this.pathFormat = pathFormat;
        this.urlFormat = urlFormat;
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
        if (path == null && StringUtils.isNotBlank(pathFormat) && MapUtils.isNotEmpty(config)) {
            try {
                path = FreemarkerUtil.transform(config, pathFormat);
            } catch (FreemarkerTransformException e) {
                path = pathFormat;
            }
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        if (url == null && StringUtils.isNotBlank(urlFormat) && MapUtils.isNotEmpty(config)) {
            try {
                url = FreemarkerUtil.transform(config, urlFormat);
            } catch (FreemarkerTransformException e) {
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

    public String getPathFormat() {
        return pathFormat;
    }

    public void setPathFormat(String pathFormat) {
        this.pathFormat = pathFormat;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
    }
}
