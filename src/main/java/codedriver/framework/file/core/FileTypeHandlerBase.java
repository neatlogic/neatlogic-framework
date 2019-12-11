package codedriver.framework.file.core;

import org.springframework.util.ClassUtils;

public abstract class FileTypeHandlerBase implements IFileTypeHandler {

	public final String getName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

}
