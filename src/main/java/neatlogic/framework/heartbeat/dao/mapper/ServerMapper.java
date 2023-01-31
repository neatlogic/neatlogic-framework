package neatlogic.framework.heartbeat.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.heartbeat.dto.ServerCounterVo;

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
	int insertServer(ServerClusterVo server);
	int replaceServerCounter(ServerCounterVo serverCounter);
	
	//DELETE
	int deleteCounterByServerId(int serverId);
}
