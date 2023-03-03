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

package neatlogic.framework.datawarehouse.core;

import neatlogic.framework.datawarehouse.dto.DataSourceAuditVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IDataSourceServiceHandler {
    /**
     * 处理器
     * @return 处理器
     */
    String getHandler();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void syncData(DataSourceVo dataSourceVo, DataSourceAuditVo reportDataSourceAuditVo);
}
