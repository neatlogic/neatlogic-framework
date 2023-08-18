package neatlogic.framework.plugin.issue;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.plugin.issue.IssueSearchVo;
import neatlogic.framework.dto.plugin.issue.SyncIssueVo;

import java.util.List;


public interface IssueSyncService {

    /**
     * 同步来源，如 JIRA，ALM， RDM
     */
    String getSource();


    /**
     * 实现类负责实现具体逻辑，获取同步结果返回
     * @param issueSearchVo
     * @return
     */
    List<SyncIssueVo> doSync(IssueSearchVo issueSearchVo) ;

    /**
     * 根据请求来源id获取请求查询条件
     * @param id
     * @return
     */
    JSONObject getQueryParameter(Long id);
}
