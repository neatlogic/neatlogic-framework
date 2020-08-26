package codedriver.framework.restful.core.privateapi;

import codedriver.framework.restful.core.JsonStreamApiComponentBase;

public abstract class PrivateJsonStreamApiComponentBase extends JsonStreamApiComponentBase implements IPrivateJsonStreamApiComponent {
    
	public int needAudit() {
		return 0;
	}
}
