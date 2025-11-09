/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
        super("config参数中“{0}”不合法,正确的config参数格式是{\"monday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"tuesday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"wednesday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"thursday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"friday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"saturday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}],\"sunday\":[{\"startTime\":\"9:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]}", msg, configTemplate);
    }
}
