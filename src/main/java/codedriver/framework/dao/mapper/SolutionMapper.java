package codedriver.framework.dao.mapper;

import codedriver.framework.dto.solution.SolutionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SolutionMapper {

	public List<SolutionVo> searchSolution(SolutionVo solutionVo);

	public SolutionVo getSolutionById(@Param("id") Long id);

	public int searchSolutionCount(SolutionVo solutionVo);

	public SolutionVo checkSolutionExistsById(@Param("id") Long id);

	public SolutionVo checkSolutionExistsByName(@Param("name") String name);

	public int updateSolutionById(SolutionVo solutionVo);

	public int insertSolution(SolutionVo solutionVo);

	public int insertEventTypeSolution(@Param("eventTypeId") Long eventTypeId,@Param("solutionId") Long solutionId);

	public int deleteEventTypeSolution(@Param("solutionId") Long solutionId);
}
