package codedriver.framework.dto.api;

import codedriver.framework.common.constvalue.CacheControlType;

/**
 * @Title: CacheControlVo
 * @Package: codedriver.framework.dto.api
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/3/3 19:09
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class CacheControlVo {
    private CacheControlType cacheControlType;
    private int maxAge;
    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public CacheControlType getCacheControlType() {
        return cacheControlType;
    }

    public void setCacheControlType(CacheControlType cacheControlType) {
        this.cacheControlType = cacheControlType;
    }
}
