package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.restful.core.RawApiComponentBase;

public abstract class PrivateRawApiComponentBase extends RawApiComponentBase implements IPrivateRawApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
