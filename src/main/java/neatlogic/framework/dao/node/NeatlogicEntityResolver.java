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

package neatlogic.framework.dao.node;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class NeatlogicEntityResolver implements EntityResolver {
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        // 检查 systemId 是否指向你的 DTD
        if (systemId.contains("mybatis_custom.dtd")) {
            // 使用类加载器加载 DTD 文件
            InputStream dtdStream = getClass().getClassLoader()
                    .getResourceAsStream("neatlogic/framework/dao/node/neatlogic_mybatis.dtd");
            return new InputSource(dtdStream);
        }
        // 如果不是目标 DTD，则返回 null，让解析器继续处理
        return null;
    }
}