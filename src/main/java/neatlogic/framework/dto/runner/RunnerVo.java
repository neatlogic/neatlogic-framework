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

package neatlogic.framework.dto.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author lvzk
 * @since 2021/4/12 14:29
 **/
public class RunnerVo extends BasePageVo implements Serializable {
    private static final long serialVersionUID = -5118893385455680746L;
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "代理分组id", type = ApiParamType.LONG)
    private Long groupId;
    @EntityField(name = "runner 名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "runner url", type = ApiParamType.STRING)
    private String url;
    @EntityField(name = "协议", type = ApiParamType.STRING)
    private String protocol;
    @EntityField(name = "runner host", type = ApiParamType.STRING)
    private String host;
    @EntityField(name = "runner port", type = ApiParamType.INTEGER)
    private Integer port;
    @EntityField(name = "runner 授权key", type = ApiParamType.STRING)
    private String accessKey;
    @EntityField(name = "runner 授权密码", type = ApiParamType.STRING)
    private String accessSecret;
    @EntityField(name = "runner 授权类型", type = ApiParamType.STRING)
    private String authType;
    @EntityField(name = "ssh公钥", type = ApiParamType.STRING)
    private String publicKey;
    @EntityField(name = "ssh私钥", type = ApiParamType.STRING)
    private String privateKey;
    @EntityField(name = "runner 分组", type = ApiParamType.JSONARRAY)
    private List<RunnerGroupVo> runnerGroupVoList;
    @EntityField(name = "NettyIp", type = ApiParamType.STRING)
    private String nettyIp;
    @EntityField(name = "Netty端口", type = ApiParamType.INTEGER)
    private String nettyPort;
    @EntityField(name = "是否认证", type = ApiParamType.INTEGER)
    private Integer isAuth;
    @EntityField(name = "被runner组引用个数", type = ApiParamType.INTEGER)
    private Integer usedCount = 0;
    @EntityField(name = "是否已删除", type = ApiParamType.INTEGER)
    private Integer isDelete;
    @EntityField(name = "是否过滤runner组", type = ApiParamType.INTEGER)
    private Integer isFilterGroup = 0;
    private List<RunnerAuthVo> runnerAuthList;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        if (StringUtils.isNotBlank(url)) {
            if (url.startsWith(HttpProtocol.HTTPS.getValue())) {
                protocol = HttpProtocol.HTTPS.getValue();
            } else if (url.startsWith(HttpProtocol.HTTP.getValue())) {
                protocol = HttpProtocol.HTTP.getValue();
            }
        }
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUrl() {
        if (StringUtils.isNotBlank(url)) {
            if (!this.url.endsWith("/")) {
                this.url = this.url + "/";
            }
        } else {
            if (StringUtils.isNotBlank(protocol) && StringUtils.isNotBlank(host) && port != null) {
                if (Config.RUNNER_CONTEXT().startsWith("/")) {
                    this.url = protocol + "://" + host + ":" + port + Config.RUNNER_CONTEXT() + "/";
                } else {
                    this.url = protocol + "://" + host + ":" + port + "/" + Config.RUNNER_CONTEXT() + "/";
                }
            }
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessKey() {
        if (CollectionUtils.isNotEmpty(runnerAuthList)) {
            return runnerAuthList.get(0).getAccessKey();
        }
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        if (CollectionUtils.isNotEmpty(runnerAuthList)) {
            return runnerAuthList.get(0).getAccessSecret();
        }
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getAuthType() {
        if (CollectionUtils.isNotEmpty(runnerAuthList)) {
            return runnerAuthList.get(0).getAuthType();
        }
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public List<RunnerGroupVo> getRunnerGroupVoList() {
        return runnerGroupVoList;
    }

    public void setRunnerGroupVoList(List<RunnerGroupVo> runnerGroupVoList) {
        this.runnerGroupVoList = runnerGroupVoList;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getNettyIp() {
        return nettyIp;
    }

    public void setNettyIp(String nettyIp) {
        this.nettyIp = nettyIp;
    }

    public String getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(String nettyPort) {
        this.nettyPort = nettyPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<RunnerAuthVo> getRunnerAuthList() {
        return runnerAuthList;
    }

    public void setRunnerAuthList(List<RunnerAuthVo> runnerAuthList) {
        this.runnerAuthList = runnerAuthList;
    }

    public Integer getIsAuth() {
        if (CollectionUtils.isNotEmpty(runnerAuthList)) {
            return 1;
        }
        return 0;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public Integer getUsedCount() {
        if (usedCount == 0 && CollectionUtils.isNotEmpty(runnerGroupVoList)) {
            return runnerGroupVoList.size();
        }
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsFilterGroup() {
        return isFilterGroup;
    }

    public void setIsFilterGroup(Integer isFilterGroup) {
        this.isFilterGroup = isFilterGroup;
    }

    public enum HttpProtocol implements IEnum {
        HTTP("http", "http"),
        HTTPS("https", "https");
        private final String value;
        private final String text;

        HttpProtocol(String value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        @Override
        public List getValueTextList() {
            JSONArray array = new JSONArray();
            for (HttpProtocol type : values()) {
                array.add(new JSONObject() {
                    {
                        this.put("value", type.getValue());
                        this.put("text", type.getText());
                    }
                });
            }
            return array;
        }
    }
}
