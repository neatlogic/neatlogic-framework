package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.TagVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {

    Long getTagIdByNameAndType(@Param("name") String name ,@Param("type") String type);

    TagVo getTagLockById(Long id);

    List<TagVo> searchTag(TagVo tagVo);

    List<TagVo> searchNoUseTag();

    List<TagVo> getTagListByIdList(List<Long> IdList);
    
    int insertTag(TagVo tagVo);

    int deleteTagById(Long id);

    int updateTag(TagVo tagVo);

}
