package neatlogic.framework.lrcode.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @Title: TreeNodeIllegalParameterException
 * @Package neatlogic.framework.tree.service
 * @Description: 移动到的目标节点不合法异常
 * @Author: linbq
 * @Date: 2021/3/17 10:03
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
public class MoveTargetNodeIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = 4478080139019340483L;

    public MoveTargetNodeIllegalException(){
        super("exception.framework.movetargetnodeillegalexception");
    }
}
