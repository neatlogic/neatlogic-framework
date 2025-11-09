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

package neatlogic.framework.exception.runner;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RunnerNotMatchException extends ApiRuntimeException {

    private static final long serialVersionUID = 3593220313941443951L;

    public RunnerNotMatchException(String ip, Long resourceId) {
        super("nfer.runnernotmatchexception.runnernotmatchexception.ipresourceid", ip, resourceId);
    }

    public RunnerNotMatchException(String ip, Long resourceId, String tag) {
        super("ip: {0}({1})找不到匹配的runner，请核对标签为{2}runner组配置", ip, resourceId, tag);
    }

    public RunnerNotMatchException() {
        super("nfer.runnernotmatchexception.runnernotmatchexception.noparam");
    }


}
