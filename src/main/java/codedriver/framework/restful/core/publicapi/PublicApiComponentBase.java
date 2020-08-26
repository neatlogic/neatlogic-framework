package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.privateapi.IPrivateApiComponent;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentBase;

public abstract class PublicApiComponentBase extends PrivateApiComponentBase implements IPrivateApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
