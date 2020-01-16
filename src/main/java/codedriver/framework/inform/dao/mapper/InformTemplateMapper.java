package codedriver.framework.inform.dao.mapper;

import java.util.List;

import codedriver.framework.inform.dto.InformTemplateVo;

public interface InformTemplateMapper {

	public int searchInformTemplateCount(InformTemplateVo informTemplateVo);

	public List<InformTemplateVo> searchInformTemplateList(InformTemplateVo informTemplateVo);

}
