/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.startup;

import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.startup.StartupBase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ResetFullTextIndexRebuildAuditHandler extends StartupBase {
    @Resource
    private FullTextIndexRebuildAuditMapper auditMapper;

    @Override
    public String getName() {
        return "重置重建全局搜索索引状态";
    }

    @Override
    public int sort() {
        return 1;
    }

    @Override
    public int executeForCurrentTenant() {
        auditMapper.resetFullTextIndexRebuildAuditStatus();
        return 0;
    }

}
