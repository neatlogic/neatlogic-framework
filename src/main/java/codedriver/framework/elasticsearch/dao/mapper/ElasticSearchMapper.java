package codedriver.framework.elasticsearch.dao.mapper;

import java.util.List;

import codedriver.framework.elasticsearch.dto.ElasticSearchAuditVo;

public interface ElasticSearchMapper {
	public List<ElasticSearchAuditVo> getElasticSearchAudit();
	
	public Integer insertElasticSearchAudit(ElasticSearchAuditVo elasticSearchAuditVo);
	
	public Integer insertElasticSearchParam(ElasticSearchAuditVo elasticSearchAuditVo);
	
	public Integer deleteElasticSearchAudit(ElasticSearchAuditVo elasticSearchAuditVo);
	
	public Integer deleteElasticSearchParam(ElasticSearchAuditVo elasticSearchAuditVo);

}
