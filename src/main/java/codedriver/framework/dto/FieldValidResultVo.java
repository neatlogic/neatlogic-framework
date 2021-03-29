package codedriver.framework.dto;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @Title: FieldValidResultVo
 * @Package: codedriver.framework.dto
 * @Description: TODO
 * @Author: 89770
 * @Date: 2021/2/20 17:10
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class FieldValidResultVo {
    private String status = "OK";
    //错误信息
    private String msg;

    private ApiRuntimeException exception;

    public FieldValidResultVo(ApiRuntimeException exception) {
        this.msg = exception.getMessage();
        this.status = "ERROR";
    }

    public FieldValidResultVo() {

    }

    public String getMsg() {
        if(exception != null){
            msg = exception.getMessage();
        }
        return msg;
    }

    public String getStatus() {
        if(exception != null){
            status = "ERROR";
        }
        return status;
    }

    public ApiRuntimeException getException() {
        return exception;
    }

    public void setException(ApiRuntimeException exception) {
        this.exception = exception;
    }
}
