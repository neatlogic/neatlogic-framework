package neatlogic.framework.lock.dao.mapper;

public interface LockMapper {

    void insertLock(String id);

    String getLockByIdForUpdate(String id);

    void deleteLock(String id);
}
