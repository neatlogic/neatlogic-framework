package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.restful.core.BinaryStreamApiComponentBase;

public abstract class PrivateBinaryStreamApiComponentBase extends BinaryStreamApiComponentBase implements IPrivateBinaryStreamApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
