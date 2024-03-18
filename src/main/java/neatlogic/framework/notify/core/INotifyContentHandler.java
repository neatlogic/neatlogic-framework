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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.notify.dto.NotifyVo;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;

public interface INotifyContentHandler {

    enum Type {
        STATIC("static", "静态"),
        DYNAMIC("dynamic", "动态");//动态插件，通知消息接收人不可指定
        private final String value;
        private final String text;

        Type(String value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Type getType(String _value) {
            for (Type e : values()) {
                if (e.value.equals(_value)) {
                    return e;
                }
            }
            return null;
        }
    }

    /**
     * @Description: 不同通知方式的预览内容与发送内容形式不一，
     * 故需要在插件内各自实现具体的方法
     * @Author: laiwt
     * @Date: 2021/3/3 14:15
     **/
    interface BuildNotifyHandler {

        String getPreviewContent(JSONObject config);

        List<NotifyVo> getNotifyVoList(JSONObject config);
    }

    interface ICondition {
        void getConditionMap(Map<String, Object> map, JSONObject conditionConfig);
    }


    String getName();

    /**
     * @Description: 获取插件类别
     * @Author: laiwt
     * @Date: 2021/1/8 18:19
     * @Params: []
     * @Returns: java.lang.String
     **/
    String getType();

    /**
     * @Description: 获取插件自带的条件，例如【待我处理的工单】插件自带【处理组】条件
     * @Author: laiwt
     * @Date: 2021/1/8 18:20
     * @Params: []
     * @Returns: com.alibaba.fastjson.JSONArray
     **/
    JSONArray getConditionOptionList();

    /**
     * @Description: 根据通知方式插件获取消息相关表单属性，例如【通知消息标题】、【通知消息内容】、【接收人】、【抄送人】
     * @Author: laiwt
     * @Date: 2021/1/8 18:20
     * @Params: [handler]
     * @Returns: com.alibaba.fastjson.JSONArray
     **/
    JSONArray getMessageAttrList(String handler);

    /**
     * @Description: 根据通知方式决定是否可选择工单显示字段
     * @Author: laiwt
     * @Date: 2021/1/8 18:21
     * @Params: []
     * @Returns: java.util.List<neatlogic.framework.common.dto.ValueTextVo>
     **/
    List<ValueTextVo> getDataColumnList(String notifyHandler);

    /**
     * @Description: 组装待发送数据
     * @Author: Aienao
     * @Date: 2021/1/8 18:21
     * @Params: [id] notify_job主键
     * @Returns: java.util.List<neatlogic.framework.notify.dto.NotifyVo>
     **/
    List<NotifyVo> getNotifyData(Long id);

    /**
     * @Description: 插件内容预览方法，不同的通知方式，发送的通知内容也不同
     * 以【待我处理的工单】为例，邮件通知的内容是工单列表，而消息通知的内容是
     * 待处理的工单数量与工单中心的链接
     * @Author: laiwt
     * @Date: 2021/2/3 14:52
     * @Params: [config, notifyHandler]
     * @Returns: java.lang.String
     **/
    String preview(JSONObject config, String notifyHandler);

    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

}
