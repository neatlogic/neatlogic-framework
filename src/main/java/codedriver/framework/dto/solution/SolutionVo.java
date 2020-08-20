package codedriver.framework.dto.solution;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.dto.event.EventTypeVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SolutionVo extends BasePageVo {

	@EntityField(name = "解决方案ID", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "解决方案名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否启用", type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "内容", type = ApiParamType.STRING)
	private String content;
	@EntityField(name = "创建人ID", type = ApiParamType.STRING)
	private String fcu;
	@EntityField(name = "更新人ID", type = ApiParamType.STRING)
	private String lcu;
	@EntityField(name = "创建时间")
	private Date fcd;
	@EntityField(name = "更新时间")
	private Date lcd;

	@EntityField(name = "创建人用户名", type = ApiParamType.STRING)
	private String fcuName;
	@EntityField(name = "更新人用户名", type = ApiParamType.STRING)
	private String lcuName;
	@EntityField(name = "关联的事件类型", type = ApiParamType.JSONARRAY)
	private List<EventTypeVo> eventTypeList = new ArrayList<>();

	public SolutionVo() {}

	public Long getId() {
		if(id == null){
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFcu() {
		return fcu;
	}

	public void setFcu(String fcu) {
		this.fcu = fcu;
	}

	public String getLcu() {
		return lcu;
	}

	public void setLcu(String lcu) {
		this.lcu = lcu;
	}

	public Date getFcd() {
		return fcd;
	}

	public void setFcd(Date fcd) {
		this.fcd = fcd;
	}

	public Date getLcd() {
		return lcd;
	}

	public void setLcd(Date lcd) {
		this.lcd = lcd;
	}

	public String getFcuName() {
		return fcuName;
	}

	public void setFcuName(String fcuName) {
		this.fcuName = fcuName;
	}

	public String getLcuName() {
		return lcuName;
	}

	public void setLcuName(String lcuName) {
		this.lcuName = lcuName;
	}

	public List<EventTypeVo> getEventTypeList() {
		return eventTypeList;
	}

	public void setEventTypeList(List<EventTypeVo> eventTypeList) {
		this.eventTypeList = eventTypeList;
	}
}
