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

public class RunnerGroupRunnerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -4324826681772207554L;

    public RunnerGroupRunnerNotFoundException(String name, String network) {
        super("执行器组“{0}”-网段“{1}”,未配置执行器(runner)", name, network);
    }

    public RunnerGroupRunnerNotFoundException(String name) {
        super("nfer.runnergrouprunnernotfoundexception.runnergrouprunnernotfoundexceptionname", name);
    }

    public RunnerGroupRunnerNotFoundException(Long id) {
        super("nfer.runnergrouprunnernotfoundexception.runnergrouprunnernotfoundexceptionid", id);
    }

    public RunnerGroupRunnerNotFoundException(Long id, String tag) {
        super("nfer.runnergrouprunnernotfoundexception.runnergrouprunnernotfoundexceptionid", id);
    }

}
