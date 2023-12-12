package neatlogic.framework.notify.dao.mapper;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.notify.dto.NotifyPolicyVo;

import java.util.List;

 public interface NotifyMapper {

	 NotifyPolicyVo getNotifyPolicyById(Long id);

	 NotifyPolicyVo getNotifyPolicyByName(String name);

	 List<NotifyPolicyVo> getNotifyPolicyListByName(String name);

	 List<NotifyPolicyVo> getNotifyPolicyListByIdList(List<Long> idList);

	 int checkNotifyPolicyNameIsRepeat(NotifyPolicyVo notifyPolicyVo);

	 List<NotifyPolicyVo> getNotifyPolicyList(NotifyPolicyVo notifyPolicyVo);

	 List<NotifyPolicyVo> getAllDefaultNotifyPolicyList();

	 List<ValueTextVo> getNotifyPolicyListForSelect(NotifyPolicyVo notifyPolicyVo);

	 NotifyPolicyVo getNotifyPolicyByHandlerLimitOne(String handler);

	 NotifyPolicyVo getDefaultNotifyPolicyByHandler(String handler);

	 int getNotifyPolicyCount(NotifyPolicyVo notifyPolicyVo);

	 int checkNotifyPolicyIsExists(Long policyId);

	 int insertNotifyPolicy(NotifyPolicyVo notifyPolicyVo);

	 int updateNotifyPolicyById(NotifyPolicyVo notifyPolicyVo);

	 int resetNotifyPolicyIsDefaultByHandler(String handler);

	 void updateNotifyPolicyIsDefaultById(Long id);

	 int deleteNotifyPolicyById(Long id);
 }
