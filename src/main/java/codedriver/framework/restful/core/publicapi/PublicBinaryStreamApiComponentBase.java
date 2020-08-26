package codedriver.framework.restful.core.publicapi;

import codedriver.framework.restful.core.BinaryStreamApiComponentBase;

public abstract class PublicBinaryStreamApiComponentBase extends BinaryStreamApiComponentBase implements IPublicBinaryStreamApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
