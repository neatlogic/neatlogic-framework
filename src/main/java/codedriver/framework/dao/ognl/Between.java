/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.ognl;

public class Between {
    public static String[] parse(String str) {
        String[] s = new String[]{"", ""};
        if (str.contains("~")) {
            String[] ss = str.split("~");
            s[0] = ss[0];
            if (ss.length > 1) {
                s[1] = str.split("~")[1];
            }
        }
        return s;
    }
}
