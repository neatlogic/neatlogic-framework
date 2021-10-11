package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.JsonStreamApiComponentBase;

public abstract class PublicJsonStreamApiComponentBase extends JsonStreamApiComponentBase implements IPublicJsonStreamApiComponent {
    
	public int needAudit() {
		return 0;
	}

	public String getToken() {
		return null;
	}
}
