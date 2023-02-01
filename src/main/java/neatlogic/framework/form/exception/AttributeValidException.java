/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.exception;

public class AttributeValidException extends Exception {
    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 8106412352004757576L;

    public AttributeValidException() {
        super();
    }

    public AttributeValidException(String msg) {
        super(msg);
    }

    public AttributeValidException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AttributeValidException(Throwable cause) {
        super(cause);
    }
}
