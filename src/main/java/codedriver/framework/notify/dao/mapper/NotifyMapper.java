package codedriver.framework.notify.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyVo;

public interface NotifyMapper {

	public NotifyPolicyVo getNotifyPolicyById(Long id);

	public int checkNotifyPolicyNameIsRepeat(NotifyPolicyVo notifyPolicyVo);

	public List<NotifyPolicyVo> getNotifyPolicyList(NotifyPolicyVo notifyPolicyVo);

	public List<ValueTextVo> getNotifyPolicyListForSelect(NotifyPolicyVo notifyPolicyVo);

	public int getNotifyPolicyCount(NotifyPolicyVo notifyPolicyVo);

	public int checkNotifyPolicyIsExists(Long policyId);

	public int insertNotifyPolicy(NotifyPolicyVo notifyPolicyVo);

	public int updateNotifyPolicyById(NotifyPolicyVo notifyPolicyVo);

	public int deleteNotifyPolicyById(Long id);

}
