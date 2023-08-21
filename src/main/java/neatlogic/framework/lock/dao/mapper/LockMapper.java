package neatlogic.framework.lock.dao.mapper;


import org.apache.ibatis.annotations.Param;

public interface LockMapper {

    void insertLock(String id);

    String getLockByIdForUpdate(String id);

    void deleteLock(String id);

    int getMysqlLock(@Param("key") String key,@Param("waitSecond")Integer waitSecond);

    int releaseMysqlLock(String key);
}
