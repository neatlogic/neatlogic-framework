package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiException;

public class PermissionDeniedException extends ApiException {

	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = 6148939003449322484L;

	public PermissionDeniedException() {
		super("没有权限进行当前操作");
	}
	

}
