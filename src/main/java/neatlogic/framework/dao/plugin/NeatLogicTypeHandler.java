package neatlogic.framework.dao.plugin;

public interface NeatLogicTypeHandler<T> {

    Object handleParameter(T parameter);
}
