/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.resubmit;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ResubmitException extends ApiRuntimeException {

    public ResubmitException(String token) {
        super("请勿重复提交到：" + token);
    }
}
