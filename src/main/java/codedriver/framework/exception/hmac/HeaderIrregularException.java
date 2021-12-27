/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.hmac;

import codedriver.framework.exception.core.ApiRuntimeException;

public class HeaderIrregularException extends ApiRuntimeException {


    public HeaderIrregularException(String header) {
        super("头部“" + header + "”不符合格式要求，请仔细查阅接口调用文档");
    }
}