/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/11/23 12:12
 **/
public class IntegrationRequestResultFormatInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = 1161601312349475176L;

    public IntegrationRequestResultFormatInvalidException() {
        super("集成请求结果不符合JSON格式");
    }
}
