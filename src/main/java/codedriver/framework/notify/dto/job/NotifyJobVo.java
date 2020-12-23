package codedriver.framework.notify.dto.job;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.notify.constvalue.NotifyRecipientType;
import codedriver.framework.notify.core.INotifyHandler;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotifyJobVo extends BaseEditorVo {
	@EntityField(name = "id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "通知消息标题", type = ApiParamType.STRING)
	private String title;
	@EntityField(name = "通知消息内容", type = ApiParamType.STRING)
	private String content;
	@EntityField(name = "cron表达式", type = ApiParamType.STRING)
	private String cron;
	@EntityField(name = "插件", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "通知方式插件", type = ApiParamType.STRING)
	private String notifyHandler;
	@EntityField(name = "配置信息", type = ApiParamType.STRING)
	private String config;
	@EntityField(name = "是否激活", type = ApiParamType.INTEGER)
	private Integer isActive;

	@EntityField(name = "收件人列表", type = ApiParamType.JSONARRAY)
	private List<String> toList;
	@EntityField(name = "抄送人列表", type = ApiParamType.JSONARRAY)
	private List<String> ccList;

	@JSONField(serialize = false)
	@EntityField(name = "接收者列表", type = ApiParamType.JSONARRAY)
	private List<NotifyJobReceiverVo> receiverList;

	@EntityField(name = "下次发送时间")
	private Date nextFireTime;
	@EntityField(name = "发送次数", type = ApiParamType.INTEGER)
	private Integer execCount;

	public Long getId() {
		if (id == null) {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getNotifyHandler() {
		return notifyHandler;
	}

	public void setNotifyHandler(String notifyHandler) {
		this.notifyHandler = notifyHandler;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public List<String> getToList() {
		if(CollectionUtils.isEmpty(toList) && CollectionUtils.isNotEmpty(receiverList)){
			toList = new ArrayList<>();
			for(NotifyJobReceiverVo vo : receiverList){
				NotifyRecipientType type = NotifyRecipientType.getNotifyRecipientType(vo.getType());
				if(type != null && INotifyHandler.RecipientType.TO.getValue().equals(vo.getReceiveType())){
					toList.add(type.getValuePlugin() + vo.getReceiver());
				}
			}
		}
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	public List<String> getCcList() {
		if(CollectionUtils.isEmpty(ccList) && CollectionUtils.isNotEmpty(receiverList)){
			ccList = new ArrayList<>();
			for(NotifyJobReceiverVo vo : receiverList){
				NotifyRecipientType type = NotifyRecipientType.getNotifyRecipientType(vo.getType());
				if(type != null && INotifyHandler.RecipientType.CC.getValue().equals(vo.getReceiveType())){
					ccList.add(type.getValuePlugin() + vo.getReceiver());
				}
			}
		}
		return ccList;
	}

	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

	public List<NotifyJobReceiverVo> getReceiverList() {
		return receiverList;
	}

	public void setReceiverList(List<NotifyJobReceiverVo> receiverList) {
		this.receiverList = receiverList;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Integer getExecCount() {
		return execCount;
	}

	public void setExecCount(Integer execCount) {
		this.execCount = execCount;
	}
}
