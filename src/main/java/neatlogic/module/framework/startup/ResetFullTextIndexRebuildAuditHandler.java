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

package neatlogic.module.framework.startup;

import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.startup.IStartup;
import neatlogic.framework.startup.StartupBase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ResetFullTextIndexRebuildAuditHandler  extends StartupBase {
    @Resource
    private FullTextIndexRebuildAuditMapper auditMapper;

    @Override
    public String getName() {
        return "重置重建全局搜索索引状态";
    }

    @Override
    public int sort() {
        return 1;
    }

    @Override
    public void executeForCurrentTenant() {
        auditMapper.resetFullTextIndexRebuildAuditStatus();
    }

    @Override
    public void executeForAllTenant() {

    }
}
