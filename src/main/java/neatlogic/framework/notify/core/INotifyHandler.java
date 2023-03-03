/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.notify.core;

import org.springframework.util.ClassUtils;

import neatlogic.framework.notify.dto.NotifyVo;

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
