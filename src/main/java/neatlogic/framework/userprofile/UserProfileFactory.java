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

package neatlogic.framework.userprofile;

import neatlogic.framework.common.constvalue.IUserProfile;
import org.reflections.Reflections;

import java.util.*;

public class UserProfileFactory {

	private static final Map<String, List<IUserProfile>> moduleId2UserProfileListMap = new HashMap<>();
	static {
		Reflections reflections = new Reflections("neatlogic");
		Set<Class<? extends IUserProfile>> userProfileClass = reflections.getSubTypesOf(IUserProfile.class);
		for (Class<? extends IUserProfile> c : userProfileClass) {
			try {
				IUserProfile[] objects = c.getEnumConstants();
				for (IUserProfile userProfile : objects) {
					moduleId2UserProfileListMap.computeIfAbsent(userProfile.getModuleId(), key -> new ArrayList<>()).add(userProfile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<IUserProfile> getUserProfileListByModuleId(String moduleId) {
		return moduleId2UserProfileListMap.get(moduleId);
	}
}
