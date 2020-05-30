package codedriver.framework.notify.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyTemplateVo;

public interface NotifyMapper {

	public int searchNotifyTemplateCount(NotifyTemplateVo notifyTemplateVo);

	public List<NotifyTemplateVo> searchNotifyTemplate(NotifyTemplateVo notifyTemplateVo);

	public NotifyTemplateVo getNotifyTemplateByUuid(String uuid);

	public List<String> getNotifyTemplateTypeList();

	public List<ValueTextVo> getNotifyTemplateListForSelect(NotifyTemplateVo notifyTemplateVo);

	public int checkNotifyTemplateNameIsRepeat(NotifyTemplateVo notifyTemplateVo);

	public int insertNotifyTemplate(NotifyTemplateVo notifyTemplate);

	public int updateNotifyTemplateByUuid(NotifyTemplateVo notifyTemplate);

	public int deleteNotifyTemplateByUuid(String uuid);

}
