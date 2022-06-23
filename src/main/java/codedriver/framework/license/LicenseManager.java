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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class LicenseManager extends ModuleInitializedListenerBase {
    static Logger logger = LoggerFactory.getLogger(LicenseManager.class);
    public static final Map<String, LicenseVo> tenantLicenseMap = new HashMap<>();
    public static final Map<String, List<String>> tenantLicenseAuthListMap = new HashMap<>();

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
    public static String getLicenseVo(String tenantUuid, String licenseStr) {
        String errorLog = StringUtils.EMPTY;
        if (StringUtils.isBlank(licenseStr)) {
            errorLog = tenantUuid + ": license invalid (blank) : " + licenseStr;
            logger.error(errorLog);
            return errorLog;
        }
        try {
            //linux生成的license
            licenseStr = licenseStr.replaceAll("\\r\\n", StringUtils.EMPTY).replaceAll("\\n", StringUtils.EMPTY).trim();
            String[] licenses = licenseStr.split("#");
            if (licenses.length != 2) {
                errorLog = tenantUuid + ": license invalid (length) : " + licenseStr;
                logger.error(errorLog);
                return errorLog;
            }
            String sign = licenses[1];
            byte[] decodeData = Base64.getDecoder().decode(licenses[0]);
            if (StringUtils.isBlank(Config.LICENSE_PK)) {
                errorLog = tenantUuid + ": license pk is blank";
                logger.error(errorLog);
                return errorLog;
            }
            if (!RSAUtils.verify(decodeData, Config.LICENSE_PK, sign)) {
                errorLog = tenantUuid + ": license invalid (verify): " + licenseStr;
                logger.error(errorLog);
                return errorLog;
            }
            String licenseData = new String(Objects.requireNonNull(RSAUtils.decryptByPublicKey(decodeData, Config.LICENSE_PK())));
            LicenseVo licenseVo = JSONObject.parseObject(licenseData).toJavaObject(LicenseVo.class);
            //校验租户是否匹配
            if (!Objects.equals(licenseVo.getTenant(), tenantUuid)) {
                errorLog = tenantUuid + ": license invalid (tenant): " + licenseStr;
                logger.error(errorLog);
                return errorLog;
            }
            //判断数据库连接串是否匹配
            if (!licenseVo.getIsDbUrlValid()) {
                errorLog = tenantUuid + ":license invalid (dbUrl) : " + licenseStr;
                logger.error(errorLog);
                return errorLog;
            }
            tenantLicenseMap.put(tenantUuid, licenseVo);
            //获取租户所有权限map
            tenantLicenseAuthListMap.put(tenantUuid, getAuthListByLicenseAuth(licenseVo));
        } catch (Exception e) {
            logger.error(tenantUuid + ": license invalid : " + e.getMessage(), e);
        }
        return errorLog;
    }

    /**
     * 根据用户权限穿透获取所有权限
     *
     * @param licenseVo license
     */
    private static List<String> getAuthListByLicenseAuth(LicenseVo licenseVo) {
        List<String> authList = new ArrayList<>();
        List<String> authActionList = new ArrayList<>();
        if (licenseVo.getAuth() != null) {
            if (CollectionUtils.isNotEmpty(licenseVo.getModuleGroupVoList())) {
                //如果moduleGroup 为all 则表示拥有所有模块的权限
                if (licenseVo.getAllAuthGroup() != null) {
                    return AuthFactory.getAuthList().stream().map(AuthBase::getAuthName).collect(Collectors.toList());
                }
                for (LicenseAuthModuleGroupVo authModuleVo : licenseVo.getModuleGroupVoList()) {
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
