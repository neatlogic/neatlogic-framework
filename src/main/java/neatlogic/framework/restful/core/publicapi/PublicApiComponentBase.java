package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.ApiComponentBase;

public abstract class PublicApiComponentBase extends ApiComponentBase implements IPublicApiComponent {

	public String getToken(){
		return null;
	}

	public int needAudit() {
		return 0;
	}
	
}
