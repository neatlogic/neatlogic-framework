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

    public List<SystemNoticeVo> searchSystemNotice(SystemNoticeVo vo);

    public int searchSystemNoticeCount(SystemNoticeVo vo);

    public List<SystemNoticeRecipientVo> getRecipientListByNoticeId(Long id);

    public int getNotReadNoticeUserCountByNoticeId(Long id);

    public int updateSystemNotice(SystemNoticeVo vo);

    public int insertSystemNotice(SystemNoticeVo vo);

    public int batchInsertSystemNoticeRecipient(List<SystemNoticeRecipientVo> recipientVoList);

    public int batchInsertSystemNoticeUser(List<SystemNoticeUserVo> recipientUserList);

    public int deleteSystemNoticeById(Long id);

    public int deleteRecipientByNoticeId(Long id);

    public int deleteNoticeUserByNoticeId(@Param("noticeId") Long id,@Param("isRead") Integer isRead,@Param("limitCount") Integer limitCount);


}
