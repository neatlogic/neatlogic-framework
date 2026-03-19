package neatlogic.framework.restful.core.privateapi.raw;

public abstract class PrivateRawApiComponentBase extends RawApiComponentBase implements IPrivateRawApiComponent {

	public int needAudit() {
		return 0;
	}
	
}
