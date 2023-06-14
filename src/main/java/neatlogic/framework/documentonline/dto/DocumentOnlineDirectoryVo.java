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

package neatlogic.framework.documentonline.dto;

import java.util.ArrayList;
import java.util.List;

public class DocumentOnlineDirectoryVo {

    private final String name;
    private final boolean isFile;
    private String path;
    private List<String> upwardNameList = new ArrayList<>();
    private List<DocumentOnlineDirectoryVo> children = new ArrayList<>();

    public DocumentOnlineDirectoryVo(String name, boolean isFile) {
        this.name = name;
        this.isFile = isFile;
    }

    public String getName() {
        return name;
    }

    public boolean getIsFile() {
        return isFile;
    }

    public List<String> getUpwardNameList() {
        return upwardNameList;
    }

    public void setUpwardNameList(List<String> upwardNameList) {
        this.upwardNameList = upwardNameList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<DocumentOnlineDirectoryVo> getChildren() {
        return children;
    }

    public void addChild(DocumentOnlineDirectoryVo child) {
        children.add(child);
    }
}
