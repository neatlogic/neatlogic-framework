package neatlogic.framework.lrcode.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @Title: TreeNodeNotFoundException
 * @Package neatlogic.framework.tree.exception
 * @Description: 树节点不存在异常
 * @Author: linbq
 * @Date: 2021/3/17 7:12
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
public class TreeNodeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 4478080139019340482L;

    public TreeNodeNotFoundException(String tableName, Object idValue) {
        super("“{0}”表中树节点：“{1}”不存在", tableName, idValue);
    }
}
