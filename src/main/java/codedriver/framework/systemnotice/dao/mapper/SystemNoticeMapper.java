package codedriver.framework.systemnotice.dao.mapper;

import codedriver.framework.systemnotice.dto.SystemNoticeRecipientVo;
import codedriver.framework.systemnotice.dto.SystemNoticeUserVo;
import codedriver.framework.systemnotice.dto.SystemNoticeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Title: SystemNoticeMapper
 * @Package: codedriver.framework.systemnotice.dao.mapper
 * @Description: 系统公告Mapper
 * @Author: laiwt
 * @Date: 2021/1/13 17:54
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface SystemNoticeMapper {

    public SystemNoticeVo getSystemNoticeBaseInfoById(Long id);

    public SystemNoticeVo getSystemNoticeById(Long id);

    public List<SystemNoticeVo> getSystemNoticeLockByIdList(List<Long> idList);

    public List<SystemNoticeVo> searchSystemNotice(SystemNoticeVo vo);

    public int searchSystemNoticeCount(SystemNoticeVo vo);

    public List<SystemNoticeRecipientVo> getRecipientListByNoticeId(Long id);

    public int getNotReadNoticeUserCountByNoticeId(Long id);

    public List<Long> getIssuedNoticeIdListByRecipientUuidList(List<String> uuidList);

    public int searchIssuedNoticeCountByUserUuid(String userUuid);

    public List<SystemNoticeVo> searchIssuedNoticeListByUserUuid(@Param("noticeVo") SystemNoticeVo vo,@Param("userUuid") String userUuid);

    public SystemNoticeVo getFirstPopUpNoticeByUserUuid(@Param("userUuid") String userUuid,@Param("noticeId") Long noticeId);

    public int checkHasNextNeedPopUpNoticeByUserUuid(@Param("userUuid") String userUuid,@Param("noticeId") Long noticeId);

    /**
     * @Description: 根据通知对象uuid获取超过生效时间的公告
     * @Author: laiwt
     * @Date: 2021/1/15 16:07
     * @Params: [uuidList]
     * @Returns: java.util.List<codedriver.framework.systemnotice.dto.SystemNoticeVo>
    **/
    public List<SystemNoticeVo> getExpiredNoticeListByRecipientUuidList(List<String> uuidList);

    /**
     * @Description: 根据通知对象uuid获取到了生效时间、状态是未发布的公告
     * @Author: laiwt
     * @Date: 2021/1/15 16:23
     * @Params: [uuidList]
     * @Returns: java.util.List<codedriver.framework.systemnotice.dto.SystemNoticeVo>
    **/
    public List<SystemNoticeVo> getHasBeenActiveNoticeListByRecipientUuidList(List<String> uuidList);

    public int updateSystemNoticeBaseInfo(SystemNoticeVo vo);

    public int updateSystemNoticeIssueInfo(SystemNoticeVo vo);

    public int updateSystemNoticeStatus(SystemNoticeVo vo);

    public int updateSystemNoticeUserReadStatus(@Param("noticeId") Long noticeId, @Param("userUuid") String userUuid);

    public int insertSystemNotice(SystemNoticeVo vo);

    public int batchInsertSystemNoticeRecipient(List<SystemNoticeRecipientVo> recipientVoList);

    public int batchInsertSystemNoticeUser(List<SystemNoticeUserVo> recipientUserList);

    public int deleteSystemNoticeById(Long id);

    public int deleteRecipientByNoticeId(Long id);

    /**
     * @Description: 批量删除system_notice_user
     * @Author: laiwt
     * @Date: 2021/1/15 17:11
     * @Params: [id, isRead, limitCount]
     * @Returns: int
    **/
    public int deleteNoticeUserByNoticeId(@Param("noticeId") Long id,@Param("isRead") Integer isRead,@Param("limitCount") Integer limitCount);

    /**
     * @Description: 根据userUuid清理system_notice_user中不存在的公告记录
     * @Author: laiwt
     * @Date: 2021/1/15 16:06
     * @Params: [uuid]
     * @Returns: int
    **/
    public int deleteNotExistsNoticeByUserUuid(String uuid);


}
