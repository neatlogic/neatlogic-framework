package neatlogic.framework.systemnotice.dao.mapper;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.systemnotice.dto.SystemNoticeRecipientVo;
import neatlogic.framework.systemnotice.dto.SystemNoticeUserVo;
import neatlogic.framework.systemnotice.dto.SystemNoticeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @Title: SystemNoticeMapper
 * @Package: neatlogic.framework.systemnotice.dao.mapper
 * @Description: 系统公告Mapper
 * @Author: laiwt
 * @Date: 2021/1/13 17:54
 **/
public interface SystemNoticeMapper {

    public SystemNoticeVo getSystemNoticeBaseInfoById(Long id);

    public int checkSystemNoticeNameRepeat(SystemNoticeVo vo);

    public SystemNoticeVo getSystemNoticeByIdAndUserUuid(@Param("noticeId") Long id,@Param("userUuid") String userUuid);

    public SystemNoticeVo getSystemNoticeById(Long id);

    public List<SystemNoticeVo> searchSystemNotice(SystemNoticeVo vo);

    public int searchSystemNoticeCount(SystemNoticeVo vo);

    public List<SystemNoticeRecipientVo> getRecipientListByNoticeId(Long id);

    public List<Long> getIssuedNoticeIdListByRecipientUuidList(List<String> uuidList);

    public int searchIssuedNoticeCountByUserUuid(@Param("userUuid") String userUuid,@Param("noticeVo") SystemNoticeVo noticeVo);

    public List<SystemNoticeVo> searchIssuedNoticeListByUserUuid(@Param("noticeVo") SystemNoticeVo vo,@Param("userUuid") String userUuid);

    public List<SystemNoticeVo> getPopUpNoticeIdListByUserUuid(@Param("userUuid") String userUuid, @Param("pageVo") BasePageVo pageVo);

    public int getPopUpNoticeCountByUserUuid(String userUuid);

    /**
     * @Description: 根据通知对象uuid获取超过生效时间的公告
     * @Author: laiwt
     * @Date: 2021/1/15 16:07
     * @Params: [uuidList]
     * @Returns: java.util.List<neatlogic.framework.systemnotice.dto.SystemNoticeVo>
    **/
    public List<SystemNoticeVo> getExpiredNoticeListByRecipientUuidList(List<String> uuidList);

    /**
     * @Description: 根据通知对象uuid获取到了生效时间、状态是未下发的公告
     * @Author: laiwt
     * @Date: 2021/1/15 16:23
     * @Params: [uuidList]
     * @Returns: java.util.List<neatlogic.framework.systemnotice.dto.SystemNoticeVo>
    **/
    public List<SystemNoticeVo> getHasBeenActiveNoticeListByRecipientUuidList(List<String> uuidList);

    /**
     * @Description: 根据user_uuid从system_notice_user中寻找已经不存在的公告ID
     * @Author: laiwt
     * @Date: 2021/1/21 17:31
     * @Params: [userUuid]
     * @Returns: java.util.List<java.lang.Long>
    **/
    public List<Long> getNotExistsNoticeIdListFromNoticeUserByUserUuid(String userUuid);

    /**
     * @Description: 在system_notice_user中寻找某个用户已经不在通知范围内的公告记录
     * @Author: laiwt
     * @Date: 2021/1/21 19:08
     * @Params: [recipientUuidList, userUuid]
     * @Returns: java.util.List<java.lang.Long>
    **/
    public List<Long> getNotInNoticeScopeNoticeIdListByUserUuid(@Param("recipientUuidList") List<String> recipientUuidList,@Param("userUuid") String userUuid);

    public int searchNoticeHistoryCountByUserUuid(@Param("userUuid") String userUuid,@Param("noticeVo") SystemNoticeVo noticeVo);

    public List<SystemNoticeVo> searchNoticeHistoryListByUserUuid(@Param("noticeVo") SystemNoticeVo vo,@Param("userUuid") String userUuid);

    public int updateSystemNoticeBaseInfo(SystemNoticeVo vo);

    public int updateSystemNoticeIssueInfo(SystemNoticeVo vo);

    public int updateSystemNoticeStatus(SystemNoticeVo vo);

    public int stopSystemNoticeById(SystemNoticeVo vo);

    public int updateSystemNoticeUserReadStatus(@Param("noticeId") Long noticeId, @Param("userUuid") String userUuid,@Param("isRead") Integer isRead);

    public int updateNoticeUserReadStatusByIdList(@Param("noticeIdList") List<Long> noticeIdList, @Param("userUuid") String userUuid);

    public int updateNotReadNoticeToReadByUserUuid(@Param("noticeVo") SystemNoticeVo vo,@Param("userUuid") String userUuid);

    public int updateReadStatusToNotReadByNoticeId(Long id);

    public int insertSystemNotice(SystemNoticeVo vo);

    public int batchInsertSystemNoticeRecipient(List<SystemNoticeRecipientVo> recipientVoList);

    public int batchInsertSystemNoticeUser(List<SystemNoticeUserVo> recipientUserList);

    public int deleteSystemNoticeById(Long id);

    public int deleteRecipientByNoticeId(Long id);

    /**
     * @Description: 根据noticeIdList清理某个用户system_notice_user中的记录
     * @Author: laiwt
     * @Date: 2021/1/15 16:06
     * @Params: [uuid]
     * @Returns: int
    **/
    public int deleteSystemNoticeUserByUserUuid(@Param("userUuid") String uuid,@Param("noticeIdList") Set<Long> noticeIdList);


}
