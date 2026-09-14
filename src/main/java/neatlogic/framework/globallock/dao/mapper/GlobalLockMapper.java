package neatlogic.framework.globallock.dao.mapper;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GlobalLockMapper {

    List<GlobalLockVo> getGlobalLockByUuid(String id);

    String getGlobalLockPkByUuidForUpdate(String uuid);

    String getGlobalLockPkByUuid(String uuid);

    GlobalLockVo getGlobalLockById(Long lockId);

    GlobalLockVo getNextGlobalLockByUuid(String uuid);

    List<String> getGlobalLockUuidByKey(@Param("handler") String handler, @Param("keyword") String keyword,@Param("handlerParamKeyword") String handlerParamKeyword);

    List<Long> getGlobalLockIdByKey(@Param("handler") String handler, @Param("keyword") String keyword,@Param("handlerParamKeyword") String handlerParamKeyword);

    void insertLock(GlobalLockVo globalLockVo);

    /** 返回实际插入行数，用于区分本次创建与已有记录，保留旧 Mapper 方法签名。 */
    int insertLockIfAbsent(GlobalLockVo globalLockVo);

    Integer updateToLockById(Long id);

    void deleteLock(Long id);

    void deleteLockByUuidList(@Param("uuidList") List<String> uuidList);

    List<GlobalLockVo> searchLock(GlobalLockVo globalLockVo);

    List<GlobalLockVo> getLockListByKeyListAndHandler(@Param("keyList") List<String> keyList, @Param("handler") String handler);

    Integer getLockCount(GlobalLockVo globalLockVo);

    void deleteLockByIdList(List<Long> idList);

    void insertLockPk(String uuid);

    /** 查询候选记录，供元数据精确筛选和分页前处理。 */
    List<GlobalLockVo> getLockCandidates(@Param("handler") String handler);

    /** 按归属索引查询，并兼容尚未回填的旧记录；调用方必须复核元数据。 */
    List<GlobalLockVo> getOwnerLockCandidates(@Param("handlers") List<String> handlers, @Param("ownerId") String ownerId);

    /** 按 ID 查询等待队列，每轮通知最多处理每个候选一次。 */
    List<GlobalLockVo> getWaitingLocks(String uuid);

    /** 在共享数据库行上分配单调递增的通知尝试顺序号。 */
    int incrementNotifyRevision(Long id);
    Long getNotifyRevision(Long id);

    /** 仅记录最新尝试的结果，不重建已删除的锁。 */
    void updateLockError(@Param("id") Long id, @Param("notify") boolean notify,
                        @Param("error") String error, @Param("revision") Long revision);
}
