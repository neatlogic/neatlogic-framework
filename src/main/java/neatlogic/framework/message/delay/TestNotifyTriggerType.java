package neatlogic.framework.message.delay;

import neatlogic.framework.notify.core.INotifyTriggerType;

/**
 * @Title: TestNotifyTriggerType
 * @Package neatlogic.framework.message.delay
 * @Description: 测试触发点，用于压测
 * @Author: linbq
 * @Date: 2021/1/12 7:49
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
public enum TestNotifyTriggerType implements INotifyTriggerType {

    TEST("test", "enum.framework.testnotifytriggertype.test", "enum.framework.testnotifytriggertype.test.1");

    private String trigger;
    private String text;
    private String description;

    private TestNotifyTriggerType(String _trigger, String _text, String _description) {
        this.trigger = _trigger;
        this.text = _text;
        this.description = _description;
    }

    @Override
    public String getTrigger() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
