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
