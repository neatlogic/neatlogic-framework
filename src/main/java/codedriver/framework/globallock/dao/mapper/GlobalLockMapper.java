package codedriver.framework.globallock.dao.mapper;

import codedriver.framework.dto.globallock.GlobalLockVo;

import java.util.List;

public interface GlobalLockMapper {

    List<GlobalLockVo> getGlobalLockByUuidForUpdate(String id);

    GlobalLockVo getGlobalLockById(Long lockId);

    GlobalLockVo getNextGlobalLockByUuid(String uuid);

    void insertLock(GlobalLockVo globalLockVo);

    Integer updateToLockById(Long id);

    void deleteLock(Long id);

}
