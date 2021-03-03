package codedriver.framework.exception.resubmit;

public class ResubmitException extends RuntimeException {

    public ResubmitException(String token) {
        super("请勿重复提交到：" + token);
    }
}
