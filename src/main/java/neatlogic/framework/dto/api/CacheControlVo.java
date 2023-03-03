package neatlogic.framework.dto.api;

import neatlogic.framework.common.constvalue.CacheControlType;

/**
 * @Title: CacheControlVo
 * @Package: neatlogic.framework.dto.api
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/3/3 19:09
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
