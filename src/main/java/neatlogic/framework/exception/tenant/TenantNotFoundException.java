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

package neatlogic.framework.exception.tenant;

import neatlogic.framework.exception.core.ApiRuntimeException;


public class TenantNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1099170784444348169L;

    public TenantNotFoundException(String tenant) {
        super("nff.jsonwebtokenvalidfilter.dofilterinternal.lacktenant", tenant);
    }

    public TenantNotFoundException(Long tenantId) {
        super("nff.jsonwebtokenvalidfilter.dofilterinternal.lacktenant", tenantId);
    }
}
