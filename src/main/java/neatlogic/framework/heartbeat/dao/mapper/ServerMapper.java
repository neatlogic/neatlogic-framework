package neatlogic.framework.heartbeat.dao.mapper;

import neatlogic.framework.dao.aop.UseMasterDatabase;
import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.heartbeat.dto.ServerCounterVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@UseMasterDatabase
public interface ServerMapper {
    //SELECT
    List<Integer> getInactivatedServerIdList(@Param("fromServerId") int fromServerId, @Param("threshold") int threshold);

    ServerClusterVo getServerLockByServerId(Integer serverId);

    List<ServerClusterVo> getOtherStartUpServerByServerId(Integer serverId);

    List<ServerClusterVo> getAllServerList();

    // 查询同一个应用服务分组下的所有服务器，用于兼容已停机服务器创建的定时作业数据。
    List<Integer> getServerIdListByGroup(String serverGroup);

    List<Integer> getStartupServerIdListByGroup(String serverGroup);

    String getUserFunctionValue();

    //UPDATE
    int updateServerByServerId(ServerClusterVo server);

    int updateServerHostByServerId(ServerClusterVo serverClusterVo);

    int resetCounterByToServerId(int toServerId);

    int updateServerHeartbeatTimeByServerId(int scheduleServerId);

    int updateServerHeartbeatTimeAndServerGroupByServerId(@Param("serverId") int serverId, @Param("serverGroup") String serverGroup);

    //INSERT
    int insertServer(ServerClusterVo server);

    int insertServerCounter(ServerCounterVo serverCounter);

    int insertServerRunTime(@Param("serverId") Integer serverId, @Param("startTime") Date startTime);

    //DELETE
    int deleteCounterByToServerId(int serverId);
}
