package codedriver.framework.server.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.server.dto.ServerClusterVo;
import codedriver.framework.server.dto.ServerNewJobVo;

public interface ServerMapper {
	//SELECT
	List<ServerClusterVo> getInactivatedServer(int currentServerId, int threshold);
	List<ServerClusterVo> getServerByStatus(String status);
	List<ServerNewJobVo> getNewJobByServerId(int currentServerId);
	
	//UPDATE
	int updateServerByServerId(ServerClusterVo server);
	int resetCounterByToServerId(int toServerId);
	int counterIncrease(@Param("fromServerId") int fromServerId, @Param("toServerId") int toServerId);
	
	//INSERT
	int insertServer(ServerClusterVo server);
	int insertServerCounter(@Param("fromServerId") int fromServerId, @Param("toServerId") int toServerId);
	
	//DELETE
	int deleteCounterByServerId(int serverId);
	
}
