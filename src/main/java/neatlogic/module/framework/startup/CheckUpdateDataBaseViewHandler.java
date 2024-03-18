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

import neatlogic.framework.rebuilddatabaseview.core.RebuildDataBaseViewManager;
import neatlogic.framework.startup.StartupBase;
import org.springframework.stereotype.Component;

@Component
public class CheckUpdateDataBaseViewHandler extends StartupBase {
    @Override
    public String getName() {
        return "nmfs.checkupdatedatabaseviewhandler.getname";
    }

    @Override
    public int sort() {
        return 11;
    }

    @Override
    public int executeForCurrentTenant() {
        RebuildDataBaseViewManager.execute();
        return 0;
    }
}
