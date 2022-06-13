/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.license;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.auth.core.AuthActionChecker;
import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dao.mapper.LicenseMapper;
import codedriver.framework.dto.DatasourceVo;
import codedriver.framework.dto.license.LicenseAuthModuleGroupVo;
import codedriver.framework.dto.license.LicenseVo;
import codedriver.framework.util.RSAUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class LicenseManager extends ModuleInitializedListenerBase {
    public static final Map<String, LicenseVo> tenantLicenseMap = new HashMap<>();

    public static final Map<String, List<String>> tenantLicenseAuthListMap = new HashMap<>();

    public static final Map<String, Map<String, List<String>>> tenantOperationListMap = new HashMap<>();

    private static boolean isExpired;

    @Resource
    private LicenseMapper licenseMapper;

    @Resource
    private DatasourceMapper datasourceMapper;

    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {

    }

    @Override
    protected void myInit() {
        List<DatasourceVo> datasourceList = datasourceMapper.getAllActiveTenantDatasource();
        if (CollectionUtils.isNotEmpty(datasourceList)) {
            List<LicenseVo> licenseVos = licenseMapper.getTenantLicenseByTenantUuidList(datasourceList.stream().map(DatasourceVo::getTenantUuid).collect(Collectors.toList()));
            for (LicenseVo license : licenseVos) {
                getLicenseVo(license.getTenant(), license.getLicenseStr());
            }
        }

    }

    /**
     * 根据租户设置license
     *
     * @param tenantUuid 租户
     * @param licenseStr license串
     */
    public static void getLicenseVo(String tenantUuid, String licenseStr) {
        if (StringUtils.isBlank(licenseStr)) {
            return;
        }
        //license = license.replaceAll(System.lineSeparator(),StringUtils.EMPTY);
        String[] licenses = licenseStr.split("\n=========================\n");
        if (licenses.length != 2) {
            return;
        }
        String sign = licenses[1];
        byte[] decodeData = Base64.getDecoder().decode(licenses[0]);
        if (!RSAUtils.verify(decodeData, Config.LICENSE_PK, sign)) {
            return;
        }
        String licenseData = new String(Objects.requireNonNull(RSAUtils.decryptByPublicKey(decodeData, Config.LICENSE_PK())));
        LicenseVo licenseVo = JSONObject.parseObject(licenseData).toJavaObject(LicenseVo.class);
        tenantLicenseMap.put(tenantUuid, licenseVo);
        //获取租户所有权限map
        tenantLicenseAuthListMap.put(tenantUuid, getAuthListByLicenseAuth(licenseVo));
    }

    /**
     * 根据用户权限穿透获取所有权限
     *
     * @param licenseVo license
     */
    private static List<String> getAuthListByLicenseAuth(LicenseVo licenseVo) {
        List<String> authList = new ArrayList<>();
        List<String> authActionList = new ArrayList<>();
        if (licenseVo != null && licenseVo.getAuth() != null) {
            List<LicenseAuthModuleGroupVo> moduleGroupList = licenseVo.getAuth().getModuleGroupList();
            //判断数据库连接串是否匹配
            if (!Config.DB_URL().startsWith(licenseVo.getDbUrl())) {
                return authList;
            }
            if (licenseVo.getExpireTime().getTime() < System.currentTimeMillis()) {
                moduleGroupList = licenseVo.getExpiredAuth().getModuleGroupList();
            }
            if (CollectionUtils.isNotEmpty(moduleGroupList)) {
                //如果moduleGroup 为all 则表示拥有所有模块的权限
                if (moduleGroupList.stream().anyMatch(o -> Objects.equals(o.getName().toUpperCase(Locale.ROOT), "ALL"))) {
                    return AuthFactory.getAuthList().stream().map(AuthBase::getAuthName).collect(Collectors.toList());
                }
                for (LicenseAuthModuleGroupVo authModuleVo : moduleGroupList) {
                    if (CollectionUtils.isNotEmpty(authModuleVo.getAuthList())) {
                        //如果auth 为all 则表示拥有这个模块所有权限
                        if (authModuleVo.getAuthList().stream().anyMatch(o -> Objects.equals(o.toUpperCase(Locale.ROOT), "ALL"))) {
                            authActionList.addAll(AuthFactory.getAuthActionListByAuthGroupList(Collections.singletonList(authModuleVo.getName())));
                        } else {
                            authActionList.addAll(authModuleVo.getAuthList());
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(authActionList)) {
                for (String s : authActionList) {
                    AuthBase authBase = AuthFactory.getAuthInstance(s);
                    if (authBase != null) {
                        authList.add(authBase.getAuthName());
                        AuthActionChecker.getAuthListByAuth(authBase, authList);
                    }
                }
            }
        }
        return authList;
    }

}
