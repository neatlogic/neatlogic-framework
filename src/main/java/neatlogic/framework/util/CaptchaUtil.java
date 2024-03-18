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

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;

import java.awt.*;

public class CaptchaUtil {
    /**
     * 获取验证码
     *
     * @return 验证码text 和 验证码base64图片
     */
    public static JSONObject getCaptcha() {
        JSONObject specCaptchaJson = new JSONObject();
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);
        specCaptchaJson.put("code", specCaptcha.text());
        specCaptchaJson.put("img", specCaptcha.toBase64());
        return specCaptchaJson;
    }
}
