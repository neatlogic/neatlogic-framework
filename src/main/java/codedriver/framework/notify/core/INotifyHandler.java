/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import org.springframework.util.ClassUtils;

import codedriver.framework.notify.dto.NotifyVo;

public interface INotifyHandler {

    enum RecipientType {
        TO("to", "接收人"),
        CC("cc", "抄送人");
        private final String value;
        private final String text;

        RecipientType(String value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static RecipientType getType(String _value) {
            for (RecipientType e : values()) {
                if (e.value.equals(_value)) {
                    return e;
                }
            }
            return null;
        }
    }

    /**
     * @Description: 处理通知
     */
    /**
     * 处理通知
     * @param notifyVo 通知信息
     * @return 通知发送成功返回true，失败返回false
     * @throws Exception 异常
     */
    boolean execute(NotifyVo notifyVo) throws Exception;

    String getType();

    /**
     * @return String
     * @Description: 插件名称
     */
    String getName();

    /**
     * @return String
     */
    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

}
