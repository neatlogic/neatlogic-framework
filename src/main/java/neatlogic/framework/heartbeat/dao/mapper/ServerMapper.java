package neatlogic.framework.heartbeat.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.heartbeat.dto.ServerCounterVo;

public interface ServerMapper {
	//SELECT
	List<Integer> getInactivatedServerIdList(@Param("fromServerId")int fromServerId, @Param("threshold")int threshold);

	ServerClusterVo getServerByServerId(Integer serverId);

	List<ServerClusterVo> getAllServerList();
	//UPDATE
	int updateServerByServerId(ServerClusterVo server);

	int updateServerHostByServerId(ServerClusterVo serverClusterVo);

	int resetCounterByToServerId(int toServerId);

	int updateServerHeartbeatTimeByServerId(int scheduleServerId);
	
	//INSERT
	int insertServer(ServerClusterVo server);

	int insertServerCounter(ServerCounterVo serverCounter);
	
	//DELETE
	int deleteCounterByToServerId(int serverId);
}
