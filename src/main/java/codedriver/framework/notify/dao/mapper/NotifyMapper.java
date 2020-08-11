package codedriver.framework.notify.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyInvokerVo;
import codedriver.framework.notify.dto.NotifyPolicyVo;

public interface NotifyMapper {

//	public int searchNotifyTemplateCount(NotifyTemplateVo notifyTemplateVo);
//
//	public List<NotifyTemplateVo> searchNotifyTemplate(NotifyTemplateVo notifyTemplateVo);
//
//	public NotifyTemplateVo getNotifyTemplateByUuid(String uuid);
//
//	public List<String> getNotifyTemplateTypeList();
//
//	public List<ValueTextVo> getNotifyTemplateListForSelect(NotifyTemplateVo notifyTemplateVo);
//
//	public int checkNotifyTemplateNameIsRepeat(NotifyTemplateVo notifyTemplateVo);
//
//	public int insertNotifyTemplate(NotifyTemplateVo notifyTemplate);
//
//	public int updateNotifyTemplateByUuid(NotifyTemplateVo notifyTemplate);
//
//	public int deleteNotifyTemplateByUuid(String uuid);

	public NotifyPolicyVo getNotifyPolicyById(Long id);

	public int checkNotifyPolicyNameIsRepeat(NotifyPolicyVo notifyPolicyVo);

	public List<NotifyPolicyVo> getNotifyPolicyList(NotifyPolicyVo notifyPolicyVo);

	public List<ValueTextVo> getNotifyPolicyListForSelect(NotifyPolicyVo notifyPolicyVo);

	public int getNotifyPolicyCount(NotifyPolicyVo notifyPolicyVo);

	public List<NotifyPolicyVo> getNotifyPolicyInvokerCountListByPolicyIdList(List<Long> policyIdList);

	public List<NotifyPolicyInvokerVo> getNotifyPolicyInvokerList(NotifyPolicyInvokerVo notifyPolicyInvokerVo);

	public int getNotifyPolicyInvokerCountByPolicyId(Long policyId);

	public int checkNotifyPolicyIsExists(Long policyId);

	public int insertNotifyPolicy(NotifyPolicyVo notifyPolicyVo);

	public int insertNotifyPolicyInvoker(NotifyPolicyInvokerVo notifyPolicyInvokerVo);

	public int updateNotifyPolicyById(NotifyPolicyVo notifyPolicyVo);

	public int deleteNotifyPolicyById(Long id);

	public int deleteNotifyPolicyInvoker(String invoker);

}
