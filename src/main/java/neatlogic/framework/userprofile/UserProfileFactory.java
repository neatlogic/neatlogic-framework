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
