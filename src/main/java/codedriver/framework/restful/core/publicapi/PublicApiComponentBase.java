package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.ApiComponentBase;

public abstract class PublicApiComponentBase extends ApiComponentBase implements IPublicApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
