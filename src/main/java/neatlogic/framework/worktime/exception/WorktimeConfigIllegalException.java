/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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
        super("exception.framework.worktimeconfigillegalexception", msg, configTemplate);
    }
}
