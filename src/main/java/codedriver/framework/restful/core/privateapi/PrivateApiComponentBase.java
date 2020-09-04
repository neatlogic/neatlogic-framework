package codedriver.framework.restful.core.privateapi;

import codedriver.framework.restful.core.ApiComponentBase;

public abstract class PrivateApiComponentBase extends ApiComponentBase implements IPrivateApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
