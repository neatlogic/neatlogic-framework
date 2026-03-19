package neatlogic.framework.restful.core.privateapi.jsonstream;

public abstract class PrivateJsonStreamApiComponentBase extends JsonStreamApiComponentBase implements IPrivateJsonStreamApiComponent {
    
	public int needAudit() {
		return 0;
	}
}
