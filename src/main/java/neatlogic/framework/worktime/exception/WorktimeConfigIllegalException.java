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

package neatlogic.framework.worktime.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class WorktimeConfigIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = -1703889762006158707L;

    private static String configTemplate = "{\"monday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]," +
            "\"tuesday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]," +
            "\"wednesday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]," +
            "\"thursday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]," +
            "\"friday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]," +
            "\"saturday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]," +
            "\"sunday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]" +
            "}";

    public WorktimeConfigIllegalException(String msg) {
        super("config参数中“{0}”不合法,正确的config参数格式是{\"monday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"tuesday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"wednesday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"thursday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"friday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"saturday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"sunday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]}", msg, configTemplate);
    }
}
