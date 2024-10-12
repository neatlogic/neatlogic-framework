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

public class RunnerGroupRunnerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -4324826681772207554L;

    public RunnerGroupRunnerNotFoundException(String name,String network) {
        super("执行器组“{0}”-网段“{1}”,未配置执行器(runner)", name);
    }

    public RunnerGroupRunnerNotFoundException(String name) {
        super("nfer.runnergrouprunnernotfoundexception.runnergrouprunnernotfoundexceptionname", name);
    }

    public RunnerGroupRunnerNotFoundException(Long id) {
        super("nfer.runnergrouprunnernotfoundexception.runnergrouprunnernotfoundexceptionid", id);
    }

    public RunnerGroupRunnerNotFoundException(Long id,String tag) {
        super("nfer.runnergrouprunnernotfoundexception.runnergrouprunnernotfoundexceptionid", id);
    }

}
