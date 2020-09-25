package codedriver.framework.elasticsearch.core;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.elasticsearch.dao.mapper.ElasticSearchMapper;
import codedriver.framework.elasticsearch.dto.ElasticSearchAuditVo;
import codedriver.framework.scheduler.core.PublicJobBase;
import codedriver.framework.scheduler.dto.JobObject;

@Component
public class ElasticSearchSelfCureJob extends PublicJobBase {
	@Autowired
	ElasticSearchMapper elasticSearchMapper;
	
	@Override
	public void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException {
		List<ElasticSearchAuditVo> auditList =  elasticSearchMapper.getElasticSearchAudit();
		for(ElasticSearchAuditVo audit :auditList) {
			JSONObject paramObj = JSONObject.parseObject(audit.getParam());
			paramObj.put("tenantUuid", jobObject.getTenantUuid());
			ElasticSearchFactory.getHandler(audit.getHandler()).save(paramObj,jobObject.getTenantUuid());
			elasticSearchMapper.deleteElasticSearchAudit(audit);
		}
		
	}

	@Override
	public String getName() {
		return "重试 批量更新 ElasticSearch 失败任务";
	}

}
