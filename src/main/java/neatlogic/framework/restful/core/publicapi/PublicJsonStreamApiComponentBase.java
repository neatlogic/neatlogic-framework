package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.JsonStreamApiComponentBase;

public abstract class PublicJsonStreamApiComponentBase extends JsonStreamApiComponentBase implements IPublicJsonStreamApiComponent {
    
	public int needAudit() {
		return 0;
	}

	public String getToken() {
		return null;
	}
}
