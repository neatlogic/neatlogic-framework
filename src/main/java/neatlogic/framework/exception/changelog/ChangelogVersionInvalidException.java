package neatlogic.framework.exception.changelog;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ChangelogVersionInvalidException extends ApiRuntimeException {

    public ChangelogVersionInvalidException(String moduleId, String date) {
        super("nfec.changelogversioninvalidexception.changelogversioninvalidexception.a", moduleId, date);
    }
}
