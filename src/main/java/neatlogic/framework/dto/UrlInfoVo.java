package neatlogic.framework.dto;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @Title: UrlInfoVo
 * @Package neatlogic.framework.dto
 * @Description: Url信息类，保存url在content中的位置
 * @Author: linbq
 * @Date: 2021/3/15 12:04
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/
public class UrlInfoVo {
    private int beginIndex;
    private int endIndex;
    private String source;
    private String target;

    public UrlInfoVo(int beginIndex, int endIndex, String source) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.source = source;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        if(StringUtils.isBlank(target) && StringUtils.isNotBlank(source)){
            //source = /test/api/binary/image/download?id=314907690737664
            String[] split = source.split("\\?");
            String path = split[0];
            String queryString = split[1];
            String tenant = path.substring(0, path.indexOf("/", 1));
            int fromIndex = path.indexOf("/api/");
            int beginIndex = fromIndex;
            fromIndex += 5;
            int endIndex = path.indexOf("/", fromIndex) + 1;
            String token = path.substring(endIndex);
            String encryptedData = RC4Util.encrypt(token + tenant + "?" + queryString);
            String homeUrl = Config.HOME_URL();
            if(StringUtils.isNotBlank(homeUrl)) {
                if (!homeUrl.endsWith(File.separator)) {
                    homeUrl += File.separator;
                }
            }
            target = homeUrl + "anonymous" + path.substring(beginIndex, endIndex) + encryptedData;
        }
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
