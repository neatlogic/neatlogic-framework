package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.TagVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TagMapper {

    Long getTagIdByNameAndType(@Param("name") String name ,@Param("type") String type);

    TagVo getTagLockById(Long id);

    TagVo getTagById(Long id);

    List<Map<String, Object>> searchTagForMatrix(MatrixDataVo matrixDataVo);

    int searchTagCountForMatrix(MatrixDataVo matrixDataVo);

    List<TagVo> searchTag(TagVo tagVo);

    List<TagVo> searchNoUseTag();

    List<TagVo> getTagListByIdList(List<Long> IdList);
    
    int insertTag(TagVo tagVo);

    int deleteTagById(Long id);

    int updateTag(TagVo tagVo);

}
