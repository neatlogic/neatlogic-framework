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

package neatlogic.module.framework.dao.mapper.doumentonline;

import neatlogic.framework.dao.aop.UseMasterDatabase;
import neatlogic.framework.documentonline.crossover.IDocumentOnlineCrossoverMapper;
import neatlogic.framework.documentonline.dto.DocumentOnlineConfigVo;

import java.util.List;

@UseMasterDatabase
public interface DocumentOnlineMapper extends IDocumentOnlineCrossoverMapper {

//    List<DocumentOnlineConfigVo> getDocumentOnlineConfigListByFilePathList(List<String> filePathList);

    List<DocumentOnlineConfigVo> getAllDocumentOnlineConfigList();

    int insertDocumentOnlineConfig(DocumentOnlineConfigVo documentOnlineConfigVo);

    int deleteDocumentOnlineConfig(DocumentOnlineConfigVo documentOnlineConfigVo);
}
