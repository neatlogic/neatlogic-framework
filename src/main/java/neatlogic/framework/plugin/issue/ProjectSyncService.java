package neatlogic.framework.plugin.issue;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.plugin.issue.ProjectSearchVo;
import neatlogic.framework.dto.plugin.issue.ProjectVo;

import java.util.List;


public interface ProjectSyncService {

    /**
     * 同步来源，如 JIRA，ALM
     */
    String getSource();


    /**
     * 实现类负责实现具体逻辑，获取同步结果返回
     * @param projectSearchVo
     * @return
     */
    List<ProjectVo> doSync(ProjectSearchVo projectSearchVo) ;

    /**
     * 根据请求来源id获取请求查询条件
     * @param id
     * @return
     */
    JSONObject getQueryParameter(Long id);
}
