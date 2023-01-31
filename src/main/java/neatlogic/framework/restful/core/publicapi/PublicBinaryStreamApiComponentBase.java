package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.BinaryStreamApiComponentBase;

public abstract class PublicBinaryStreamApiComponentBase extends BinaryStreamApiComponentBase implements IPublicBinaryStreamApiComponent {

    public int needAudit() {
        return 0;
    }

    public String getToken() {
        return null;
    }
}
