package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.restful.core.JsonStreamApiComponentBase;

public abstract class PrivateJsonStreamApiComponentBase extends JsonStreamApiComponentBase implements IPrivateJsonStreamApiComponent {
    
	public int needAudit() {
		return 0;
	}
}
