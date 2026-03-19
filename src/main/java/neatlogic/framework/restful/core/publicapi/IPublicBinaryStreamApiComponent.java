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

package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.privateapi.binarystream.IBinaryStreamApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author
 * @Time Aug 26,2020
 * @Description: 外部接口
 */
@Deprecated
public interface IPublicBinaryStreamApiComponent extends IBinaryStreamApiComponent {
    /**
     * 接口唯一标识，也是访问URI
     *
     * @return token
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();
}
