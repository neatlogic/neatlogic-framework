package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.restful.core.ApiComponentBase;

public abstract class PrivateApiComponentBase extends ApiComponentBase implements IPrivateApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
