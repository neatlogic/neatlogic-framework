package codedriver.framework.dao.mapper;

import codedriver.framework.dto.TagVo;

import java.util.List;

public interface TagMapper {

    public List<TagVo> searchTag(TagVo tagVo);

    public List<TagVo> getTagListByIdList(List<Long> IdList);
    
    public int insertTag(TagVo tagVo);

    public int deleteTagById(Long id);

    public int updateTag(TagVo tagVo);

}
