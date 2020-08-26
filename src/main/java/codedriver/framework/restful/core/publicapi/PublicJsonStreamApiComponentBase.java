package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.JsonStreamApiComponentBase;
import codedriver.framework.restful.core.MyJsonStreamApiComponent;

public abstract class PublicJsonStreamApiComponentBase extends JsonStreamApiComponentBase implements MyJsonStreamApiComponent {
    
	public int needAudit() {
		return 0;
	}
}
