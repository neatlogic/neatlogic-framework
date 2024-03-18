/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.$;

/**
 * @Description: Cache-control头类型
 * no-store          没有缓存         缓存中不得存储任何关于客户端请求和服务端响应的内容。每次由客户端发起的请求都会下载完整的响应内容。
 * no-cache          缓存但重新验证   如下头部定义，此方式下，每次有请求发出时，缓存会将此请求发到服务器（译者注：该请求应该会带有与本地缓存相关的验证字段）
 * private           私有缓存        则表示该响应是专用于某单个用户的，中间人不能缓存此响应，该响应只能应用于浏览器私有缓存中。
 * public            公共缓存        指令表示该响应可以被任何中间人（译者注：比如中间代理、CDN等）缓存。若指定了"public"，则一些通常不被中间人缓存的页面（译者注：因为默认是private）（比如 带有HTTP验证信息（账号密码）的页面 或 某些特定状态码的页面），将会被其缓存。
 * max-age=xxx       过期           xxx表示秒，过期机制中，最重要的指令是"max-age=<seconds>"，表示资源能够被缓存（保持新鲜）的最大时间。相对Expires而言，max-age是距离请求发起的时间的秒数。针对应用中那些不会改变的文件，通常可以手动设置一定的时长以保证缓存有效，例如图片、css、js等静态资源。
 * must-revalidate   验证方式       当使用了"must-revalidate" 指令，那就意味着缓存在考虑使用一个陈旧的资源时，必须先验证它的状态，已过期的缓存将不被使用。详情看下文关于缓存校验的内容。
 * @Author: 89770
 * @Date: 2021/3/3 18:34
 * @Params: * @param null:
 * @Returns: * @return: null
 **/
public enum CacheControlType {
    NOSTORE("no-store", "common.nocache"),
    NOCACHE("no-cache", "common.cacheandvalid"),
    PRIVATE("private", "common.privatecache"),
    PUBLIC("public", "common.publiccache"),
    MAXAGE("max-age", "common.expired"),
    MUSTREVALIDATE("must-revalidate", "common.validtype");
    private final String value;
    private final String text;

    private CacheControlType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getText(String value) {
        for (CacheControlType type : values()) {
            if (type.getValue().equals(value)) {
                return type.getText();
            }
        }
        return "";
    }
}
