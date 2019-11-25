package codedriver.framework.server.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.server.dto.ServerClusterVo;

public interface ServerMapper {
	//SELECT
	List<ServerClusterVo> getInactivatedServer(@Param("currentServerId")int currentServerId, @Param("threshold")int threshold);
	List<ServerClusterVo> getServerByStatus(String status);
	
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
