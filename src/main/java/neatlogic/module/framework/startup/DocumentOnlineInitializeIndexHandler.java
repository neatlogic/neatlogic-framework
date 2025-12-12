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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.documentonline.util.DocumentOnlineManager;
import neatlogic.framework.startup.StartupBase;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

@Component
public class DocumentOnlineInitializeIndexHandler extends StartupBase {

    @Override
    public String getName() {
        return "nmfs.documentonlineinitializeindexhandler.getname";
    }

    @Override
    public int executeForAllTenant() {
        // 如果war外部路径(路径通过config.properties配置文件中documentonline.home变量设置)中有jar包，优先从外部加载说明文档，如果没有，再尝试从war内部加载说明文档
        JSONObject resultObj = DocumentOnlineManager.LoadDocumentsOutsideWar();
        if (MapUtils.isEmpty(resultObj)) {
            DocumentOnlineManager.LoadDocumentsWithinWar();
        }
        return 1;
    }

    @Override
    public int sort() {
        return 10;
    }
}
