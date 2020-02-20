package codedriver.framework.auth.core;

public abstract class AuthBase {
	public final String getAuthName() {
		return this.getClass().getSimpleName();
	}

	public abstract String getAuthDisplayName();

	public abstract String getAuthIntroduction();

	public abstract String getAuthGroup();

}
