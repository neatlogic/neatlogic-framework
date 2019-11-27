package codedriver.framework.server.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.server.dto.ServerClusterVo;
import codedriver.framework.server.dto.ServerCounterVo;

public interface ServerMapper {
	//SELECT
	List<ServerClusterVo> getInactivatedServer(@Param("currentServerId")int currentServerId, @Param("threshold")int threshold);
	List<ServerClusterVo> getServerByStatus(String status);
	ServerClusterVo getServerByServerId(Integer serverId);
	List<ServerCounterVo> getServerCounterIncreaseByFromServerId(Integer fromServerId);
	//UPDATE
	int updateServerByServerId(ServerClusterVo server);
	int resetCounterByToServerId(int toServerId);
	
	//INSERT
	int insertServer(ServerClusterVo server);
	int insertServerCounter(ServerCounterVo serverCounter);
	
	//DELETE
	int deleteCounterByServerId(int serverId);		
}
