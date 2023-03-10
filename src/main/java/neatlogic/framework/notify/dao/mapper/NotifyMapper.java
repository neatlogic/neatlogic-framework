package neatlogic.framework.notify.dao.mapper;

import java.util.List;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.notify.dto.NotifyPolicyVo;

 public interface NotifyMapper {

	 NotifyPolicyVo getNotifyPolicyById(Long id);

	 NotifyPolicyVo getNotifyPolicyByName(String name);

	 List<NotifyPolicyVo> getNotifyPolicyListByIdList(List<Long> idList);

	 int checkNotifyPolicyNameIsRepeat(NotifyPolicyVo notifyPolicyVo);

	 List<NotifyPolicyVo> getNotifyPolicyList(NotifyPolicyVo notifyPolicyVo);

	 List<ValueTextVo> getNotifyPolicyListForSelect(NotifyPolicyVo notifyPolicyVo);

	 NotifyPolicyVo getNotifyPolicyByHandlerLimitOne(String handler);

	 int getNotifyPolicyCount(NotifyPolicyVo notifyPolicyVo);

	 int checkNotifyPolicyIsExists(Long policyId);

	 int insertNotifyPolicy(NotifyPolicyVo notifyPolicyVo);

	 int updateNotifyPolicyById(NotifyPolicyVo notifyPolicyVo);

	 int deleteNotifyPolicyById(Long id);

}
