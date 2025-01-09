/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.common.constvalue.systemuser;

import neatlogic.framework.dto.UserVo;
import neatlogic.framework.util.I18nUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SystemUserFactory {
    private static final Map<String, ISystemUser> systemUserMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SystemUserFactory.class);

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends ISystemUser>> systemClass = reflections.getSubTypesOf(ISystemUser.class);

        for (Class<? extends ISystemUser> c : systemClass) {
            try {
                if (!c.isInterface()) {
                    try {
                        Object instance;
                        Object[] objects = c.getEnumConstants();
                        if (objects != null && objects.length > 0) {
                            instance = objects[0];
                        } else {
                            instance = c.newInstance();
                        }
                        Object obj = c.getMethod("getSystemUserList").invoke(instance);
                        if (obj != null) {
                            List<ISystemUser> systemUsers = (List<ISystemUser>) obj;
                            for (ISystemUser systemUser : systemUsers) {
                                if (systemUserMap.containsKey(systemUser.getUserId())) {
                                    String errorMsg = I18nUtils.getStaticMessage("nfccs.systemuserfactory.repeatexception");
                                    logger.error(errorMsg);
                                }
                                systemUserMap.put(systemUser.getUserId(), systemUser);
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static UserVo getUserVoByUser(String user) {
        for (Map.Entry<String, ISystemUser> userEntry : systemUserMap.entrySet()) {
            ISystemUser systemUser = userEntry.getValue();
            if (systemUser.getUserUuid().equals(user) || systemUser.getUserId().equals(user)) {
                return systemUser.getUserVo();
            }
        }
        return null;
    }

    public static String getUserTokenByUser(String user) {
        for (Map.Entry<String, ISystemUser> userEntry : systemUserMap.entrySet()) {
            ISystemUser systemUser = userEntry.getValue();
            if (systemUser.getUserUuid().equals(user) || systemUser.getUserId().equals(user)) {
                return systemUser.getToken();
            }
        }
        return null;
    }

    public static ISystemUser getSystemUserByUserId(String userId) {
        return systemUserMap.get(userId);
    }
}
