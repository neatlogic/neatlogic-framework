/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.exception.runner;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class RunnerGroupIdNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -7392923556010588732L;

    public RunnerGroupIdNotFoundException(Long id) {
        super("runner组id:{0}不存在", id);
    }

    public RunnerGroupIdNotFoundException(String agentIp) {
        super("通过tagentIp：{0}找不到match network 的 runner组id", agentIp);
    }
}
