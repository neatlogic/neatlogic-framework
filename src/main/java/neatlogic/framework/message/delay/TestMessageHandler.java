package neatlogic.framework.message.delay;

import neatlogic.framework.message.core.MessageHandlerBase;
import neatlogic.framework.notify.dto.NotifyVo;

import java.util.List;

/**
 * @Title: TestMessageHandler
 * @Package neatlogic.framework.message.delay
 * @Description: 消息测试类型，用于压测
 * @Author: linbq
 * @Date: 2021/1/12 7:55
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/
public class TestMessageHandler extends MessageHandlerBase {
    @Override
    public String getName() {
        return "测试类型";
    }

    @Override
    public String getDescription() {
        return "用于测试消息缓存";
    }

    @Override
    public boolean getNeedCompression() {
        return false;
    }

    @Override
    public NotifyVo compress(List<NotifyVo> notifyVoList) {
        return null;
    }
}
