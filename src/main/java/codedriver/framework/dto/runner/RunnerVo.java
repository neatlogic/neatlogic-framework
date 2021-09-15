/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.runner;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

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
    @EntityField(name = "代理分组id",type = ApiParamType.LONG)
    private Long groupId;
    @EntityField(name = "runner 名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "runner url", type = ApiParamType.STRING)
    private String url;
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
    private List<RunnerGroupVo> proxyGroupVoList;
    @EntityField(name = "NettyIp",type = ApiParamType.STRING)
    private String nettyIp;
    @EntityField(name = "Netty端口",type = ApiParamType.INTEGER)
    private String nettyPort;

    public Long getId() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getAuthType() {
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

    public List<RunnerGroupVo> getProxyGroupVoList() {
        return proxyGroupVoList;
    }

    public void setProxyGroupVoList(List<RunnerGroupVo> proxyGroupVoList) {
        this.proxyGroupVoList = proxyGroupVoList;
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
}
