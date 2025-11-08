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

package neatlogic.module.framework.startup;

import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.startup.StartupBase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ResetFullTextIndexRebuildAuditHandler extends StartupBase {
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
    public int executeForCurrentTenant() {
        auditMapper.resetFullTextIndexRebuildAuditStatus();
        return 0;
    }

}
