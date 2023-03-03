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

package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author
 * @Time Aug 26,2020
 * @Description: 外部接口
 */
public interface IPublicBinaryStreamApiComponent extends IBinaryStreamApiComponent {
    /**
     * 接口唯一标识，也是访问URI
     *
     * @return token
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();
}
