package codedriver.framework.counter.core;

public abstract class GlobalCounterBase implements IGlobalCounter {
	@Override
	public Object getShowData() {
		return getMyShowData();
	}

	public abstract Object getMyShowData();
}
