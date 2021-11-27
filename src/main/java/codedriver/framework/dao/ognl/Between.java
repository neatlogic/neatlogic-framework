/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.ognl;

/**
 * 处理配置项查询属性between表达式，一定会返回一个长度为2的数组，代表前后两个值，如果处理不了则返回最小值和最大值，保证sql不出错
 */
public class Between {
    public static Double[] parse(String str) {
        Double[] s = new Double[]{null, null};
        if (str.contains("~")) {
            String[] ss = str.split("~");
            try {
                s[0] = Double.parseDouble(ss[0]);
            } catch (Exception ignored) {

            }
            if (ss.length > 1) {
                try {
                    s[1] = Double.parseDouble(ss[1]);
                } catch (Exception ignored) {

                }
            }
        }
        if (s[0] == null && s[1] == null) {
            s[0] = -Double.MAX_VALUE;
            s[1] = Double.MAX_VALUE;
        }
        return s;
    }
}
