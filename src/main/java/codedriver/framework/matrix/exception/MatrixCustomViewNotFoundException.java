/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixCustomViewNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 1808429617492197984L;

	public MatrixCustomViewNotFoundException(String name) {
		super("矩阵：'" + name + "的自定义视图信息不存在");
	}
}
