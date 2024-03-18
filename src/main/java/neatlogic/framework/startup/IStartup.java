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

package neatlogic.framework.startup;

public interface IStartup {
    /**
     * 作业名称
     *
     * @return 字符串
     */
    String getName();

    /**
     * 每个租户分别执行
     */
    default int executeForCurrentTenant() {
        //返回-999代表没有任何执行逻辑
        return -999;
    }

    /*
    只执行一次，晚于executeForTenant执行
     */
    default int executeForAllTenant() {
        //返回-999代表没有任何执行逻辑
        return -999;
    }

    /**
     * 排序
     *
     * @return 顺序
     */
    int sort();

    /**
     * 设置所在模块组名
     *
     * @param group 模块组
     */
    void setGroup(String group);

    /**
     * 获取所在模块组
     *
     * @return 模块组
     */
    String getGroup();
}
