package neatlogic.framework.dto;

import neatlogic.framework.exception.core.ApiRuntimeException;
import com.alibaba.fastjson.JSONObject;

/**
 * @Title: FieldValidResultVo
 * @Package: neatlogic.framework.dto
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/2/20 17:10
 **/
public class FieldValidResultVo {
    private String status = "OK";
    //错误信息
    private String msg;

    private JSONObject param;

    private ApiRuntimeException exception;

    public FieldValidResultVo(ApiRuntimeException exception) {
        this.msg = exception.getMessage();
        this.status = "ERROR";
        this.param = exception.getParam();
    }

    public FieldValidResultVo() {

    }

    public String getMsg() {
        if (exception != null) {
            msg = exception.getMessage();
        }
        return msg;
    }

    public String getStatus() {
        if (exception != null) {
            status = "ERROR";
        }
        return status;
    }

    public Object getParam() {
        if (exception != null) {
            param = exception.getParam();
        }
        return param;
    }

    public ApiRuntimeException getException() {
        return exception;
    }

    public void setException(ApiRuntimeException exception) {
        this.exception = exception;
    }
}
