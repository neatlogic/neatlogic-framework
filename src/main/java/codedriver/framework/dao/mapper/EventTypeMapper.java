package codedriver.framework.dao.mapper;

import codedriver.framework.dto.AuthorityVo;
import codedriver.framework.dto.event.EventTypeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EventTypeMapper {

	public List<EventTypeVo> getEventTypeByParentId(Long parentId);

	public int updateEventTypeLeftRightCode(@Param("id") Long id, @Param("lft") int lft, @Param("rht") int rht);

	public Integer getMaxRhtCode();

	public int searchEventTypeCount(EventTypeVo eventTypeVo);

	public List<EventTypeVo> searchEventType(EventTypeVo eventTypeVo);

	public List<EventTypeVo> getEventTypeSolutionCountAndChildCountListByIdList(List<Long> eventTypeIdList);

	public int checkEventTypeIsExists(Long id);

	public int updateEventTypeNameById(EventTypeVo eventTypeVo);

	public int getEventTypeCountOnLock();

	public int checkLeftRightCodeIsWrong();

	public EventTypeVo getEventTypeById(Long id);

	public int batchUpdateEventTypeLeftCode(@Param("minCode")Integer minCode, @Param("step") int step);

	public int batchUpdateEventTypeRightCode(@Param("minCode")Integer minCode, @Param("step") int step);

	public  int insertEventType(EventTypeVo eventTypeVo);

	public int insertEventTypeAuthority(@Param("authorityVo") AuthorityVo authority, @Param("eventTypeId") Long eventTypeId);

}
