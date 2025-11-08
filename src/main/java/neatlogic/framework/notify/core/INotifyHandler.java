/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.notify.core;

import neatlogic.framework.notify.dto.NotifyVo;
import org.springframework.util.ClassUtils;

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
