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
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.dto.license.LicenseAuthModuleVo;
import codedriver.framework.dto.license.LicenseVo;
import codedriver.framework.util.RSAUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class LicenseManager extends ModuleInitializedListenerBase {
    public static final Map<String, LicenseVo> tenantLicenseMap = new HashMap<>();

    public static final Map<String, List<UserAuthVo>> tenantLicenseAuthListMap = new HashMap<>();

    public static final Map<String, List<String>> tenantOperationListMap = new HashMap<>();

    private static boolean isExpired;

    @Autowired
    private LicenseMapper licenseMapper;

    @Autowired
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

    private void getLicenseVo(String tenantUuid, String licenseStr) {
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
        //获取租户所有操作map
        tenantOperationListMap.put(tenantUuid,getOperationTypeListByLicenseAuth(licenseVo));
    }

    /**
     * 根据license获取operationTypeList
     * @param licenseVo license
     * @return operationList
     */
    private List<String> getOperationTypeListByLicenseAuth(LicenseVo licenseVo) {
        List<String> operationTypeList = new ArrayList<>();
        if(licenseVo != null && licenseVo.getAuth() != null && CollectionUtils.isNotEmpty(licenseVo.getAuth().getModuleList())){}
        return operationTypeList;
    }

    /**
     * 根据用户权限穿透获取所有权限
     *
     * @param licenseVo license
     */
    private static List<UserAuthVo> getAuthListByLicenseAuth(LicenseVo licenseVo) {
        List<UserAuthVo> userAuthVoList = new ArrayList<>();
        List<String> authActionList = new ArrayList<>();
        if (licenseVo != null && licenseVo.getAuth() != null) {
            List<LicenseAuthModuleVo> moduleGroupList = licenseVo.getAuth().getModuleList();
            //判断数据库连接串是否匹配
            if(!Config.DB_URL().startsWith(licenseVo.getDbUrl())){
                return userAuthVoList;
            }
            if (licenseVo.getExpireTime().getTime() < System.currentTimeMillis()) {
                moduleGroupList = licenseVo.getExpiredAuth().getModuleList();
            }
            if (CollectionUtils.isNotEmpty(moduleGroupList)) {
                for (LicenseAuthModuleVo authModuleVo : moduleGroupList) {
                    if (CollectionUtils.isNotEmpty(authModuleVo.getAuthList())) {
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
                        userAuthVoList.add(new UserAuthVo(authBase));
                        AuthActionChecker.getAuthListByAuth(authBase, userAuthVoList);
                    }
                }
            }
        }
        return userAuthVoList;
    }

}
