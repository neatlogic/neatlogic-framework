package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 匿名接口要求的签名缺失或校验不通过。 */
public class AnonymousApiSignatureInvalidException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;

    public AnonymousApiSignatureInvalidException() {
        super("nf.api.anonymous.signature.invalid");
    }
}
