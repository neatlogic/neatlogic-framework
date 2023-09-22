package neatlogic.framework.exception.changelog;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ChangelogVersionNotFoundException extends ApiRuntimeException {

    public ChangelogVersionNotFoundException(String moduleId, String date) {
        super("nfec.changelogversionnotfoundexception.changelogversionnotfoundexception.a", moduleId, date);
    }
}
