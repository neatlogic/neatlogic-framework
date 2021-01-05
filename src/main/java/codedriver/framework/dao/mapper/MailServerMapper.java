package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.MailServerVo;
import codedriver.framework.notify.dto.NotifyVo;
import org.apache.ibatis.annotations.Param;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 16:44
 **/
public interface MailServerMapper {

    public MailServerVo getActiveMailServer();

	public MailServerVo getMailServerByUuid(String uuid);

	public int searchMailServerCount(MailServerVo mailServerVo);

	public List<MailServerVo> searchMailServerList(MailServerVo mailServerVo);

	public int checkMailServerIsExists(String uuid);

	public int checkMailServerNameIsRepeat(MailServerVo mailServerVo);

	public int replaceMailServer(MailServerVo mailServerVo);

	public int resetAllMailServerStatus();

	public int insertMailHistory(NotifyVo notifyVo);

	public int activeMailServerByUuid(String uuid);

	public int updateMailServerByUuid(MailServerVo mailServerVo);

	public int updateMailHistoryStatusAndFailureReasonById(@Param("id") Long id, @Param("failureReason") String failureReason);

	public int deleteMailServerByUuid(String string);
}
