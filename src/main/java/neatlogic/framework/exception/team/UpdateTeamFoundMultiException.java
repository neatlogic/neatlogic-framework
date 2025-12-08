package neatlogic.framework.exception.team;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class UpdateTeamFoundMultiException extends ApiRuntimeException {

    @Serial
    private static final long serialVersionUID = -3122960050146480966L;

    public UpdateTeamFoundMultiException(String teamUuid) {
        super("nfet.updateteamfoundmultiexception.updateteamfoundmultiexception", teamUuid);
    }
}
