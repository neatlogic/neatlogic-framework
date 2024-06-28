package neatlogic.framework.globallock.dao.mapper;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GlobalLockMapper {

    List<GlobalLockVo> getGlobalLockByUuidForUpdate(String id);

    GlobalLockVo getGlobalLockById(Long lockId);

    GlobalLockVo getNextGlobalLockByUuid(String uuid);

    List<String> getGlobalLockUuidByKey(@Param("handler") String handler, @Param("keyword") String keyword,@Param("handlerParamKeyword") String handlerParamKeyword);

    void insertLock(GlobalLockVo globalLockVo);

    Integer updateToLockById(Long id);

    void deleteLock(Long id);

    void deleteLockByUuidList(@Param("uuidList") List<String> uuidList);

    List<GlobalLockVo> searchLock(GlobalLockVo globalLockVo);

    List<GlobalLockVo> getLockListByKeyListAndHandler(@Param("keyList") List<String> keyList, @Param("handler") String handler);

    Integer getLockCount(GlobalLockVo globalLockVo);

}
