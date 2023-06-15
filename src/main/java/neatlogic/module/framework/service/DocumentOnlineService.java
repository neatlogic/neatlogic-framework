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

package neatlogic.module.framework.service;

import neatlogic.framework.documentonline.dto.DocumentOnlineDirectoryVo;
import neatlogic.framework.documentonline.dto.DocumentOnlineVo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DocumentOnlineService {
    /**
     * 从流中先跳过skip个字符，再截取number个字符返回
     * @param inputStream
     * @param skip 跳过字符个数
     * @param number 截取字符个数
     * @return 返回结果
     * @throws IOException
     */
    String interceptsSpecifiedNumberOfCharacters(InputStream inputStream, int skip, int number) throws IOException;

    /**
     * 通过递归，获取某个目录下的指定模块、指定菜单下的文件
     * @param directory 目录
     * @param moduleGroup 指定模块
     * @param menu 指定菜单
     * @return 返回文件列表
     */
    List<DocumentOnlineVo> getAllFileList(DocumentOnlineDirectoryVo directory, String moduleGroup, String menu);

    /**
     * 通过递归，获取某个目录下的所有文件
     * @param directory 目录
     * @return 返回文件列表
     */
    List<DocumentOnlineVo> getAllFileList(DocumentOnlineDirectoryVo directory);
}
