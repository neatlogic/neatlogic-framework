package codedriver.framework.heartbeat.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.heartbeat.dto.ServerClusterVo;
import codedriver.framework.heartbeat.dto.ServerCounterVo;

public interface ServerMapper {
	//SELECT
	List<ServerClusterVo> getInactivatedServer(@Param("currentServerId")int currentServerId, @Param("threshold")int threshold);
	List<ServerClusterVo> getServerByStatus(String status);
	ServerClusterVo getServerByServerId(Integer serverId);
	List<ServerCounterVo> getServerCounterIncreaseByFromServerId(Integer fromServerId);
	//UPDATE
	int updateServerByServerId(ServerClusterVo server);

	int resetCounterByToServerId(int toServerId);

	int updateServerCounterIncrementByOneByFromServerId(Integer fromServerId);
	
	//INSERT
	int replaceServer(ServerClusterVo server);
	int replaceServerCounter(ServerCounterVo serverCounter);
	
	//DELETE
	int deleteCounterByServerId(int serverId);
}
