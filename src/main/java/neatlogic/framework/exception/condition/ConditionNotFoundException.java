package neatlogic.framework.exception.condition;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ConditionNotFoundException extends ApiRuntimeException {


	private static final long serialVersionUID = 5850556181926874174L;

	public ConditionNotFoundException(String condition) {
		super("条件：'" + condition + "'不存在");
	}
}
