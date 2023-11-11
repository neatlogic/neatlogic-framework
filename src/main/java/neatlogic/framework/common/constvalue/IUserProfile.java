package neatlogic.framework.common.constvalue;

import java.util.List;

public interface IUserProfile {
	String getValue();
	String getText();
	List<IUserProfileOperate> getProfileOperateList();
	String getModuleId();
	
}
