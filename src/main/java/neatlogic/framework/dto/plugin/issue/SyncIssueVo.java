package neatlogic.framework.dto.plugin.issue;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;

import java.util.Date;


public class SyncIssueVo extends BasePageVo {

    /**
     * id
     */
    @EntityField(name = "需求id", type = ApiParamType.LONG)
    private Long id;

    /**
     * sourceId
     */
    @EntityField(name = "同步来源id", type = ApiParamType.LONG)
    private Long sourceId;

    /**
     * source
     */
    @EntityField(name = "同步来源", type = ApiParamType.STRING)
    private String source;

    /**
     * no
     */
    @EntityField(name = "issue编号", type = ApiParamType.STRING)
    private String no;

    /**
     * name
     */
    @EntityField(name = "issue名称", type = ApiParamType.STRING)
    private String name;


    /**
     * type
     */
    @EntityField(name = "issue类型", type = ApiParamType.STRING)
    private String type;

    /**
     * type
     */
    @EntityField(name = "是否为有效需求，true: 有效，false：无效", type = ApiParamType.BOOLEAN)
    private Boolean isValid = false;

    /**
     * status
     */
    @EntityField(name = "issue状态", type = ApiParamType.STRING)
    private String status;

    /**
     * description
     */
    @EntityField(name = "issue描述", type = ApiParamType.STRING)
    private String description;

    /**
     * handleUserId
     */
    @EntityField(name = "issue处理人", type = ApiParamType.STRING)
    private String handleUserId;

    /**
     * issueCreateTime
     */
    @EntityField(name = "issue创建时间", type = ApiParamType.STRING)
    private Date issueCreateTime = null;

    /**
     * issueUpdateTime
     */
    @EntityField(name = "issue更新时间", type = ApiParamType.STRING)
    private Date issueUpdateTime = null;

    /**
     * issueLastSyncTime
     */
    @EntityField(name = "issue最后同步时间", type = ApiParamType.STRING)
    private Date issueLastSyncTime = null;

    /**
     * issueCreator
     */
    @EntityField(name = "issue创建人", type = ApiParamType.STRING)
    private String issueCreator = null;

    /**
     * issueUpdateUser
     */
    @EntityField(name = "issue更新人", type = ApiParamType.STRING)
    private String issueUpdateUser = null;

    /**
     * issuePersonIncharge
     */
    @EntityField(name = "issue负责人", type = ApiParamType.STRING)
    private String issuePersonIncharge = null;

    /**
     * issueSyncUser
     */
    @EntityField(name = "issue同步用户", type = ApiParamType.STRING)
    private String issueSyncUser;

    /**
     * fcd
     */
    @EntityField(name = "创建时间", type = ApiParamType.STRING)
    private Date fcd;

    /**
     * fcu
     */
    @EntityField(name = "创建人", type = ApiParamType.STRING)
    private String fcu;

    /**
     * lcd
     */
    @EntityField(name = "最后修改时间", type = ApiParamType.STRING)
    private Date lcd;

    /**
     * lcu
     */
    @EntityField(name = "最后修改用户", type = ApiParamType.STRING)
    private String lcu;


    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHandleUserId() {
        return handleUserId;
    }

    public void setHandleUserId(String handleUserId) {
        this.handleUserId = handleUserId;
    }

    public Date getIssueCreateTime() {
        return issueCreateTime;
    }

    public void setIssueCreateTime(Date issueCreateTime) {
        this.issueCreateTime = issueCreateTime;
    }

    public Date getIssueUpdateTime() {
        return issueUpdateTime;
    }

    public void setIssueUpdateTime(Date issueUpdateTime) {
        this.issueUpdateTime = issueUpdateTime;
    }

    public Date getIssueLastSyncTime() {
        return issueLastSyncTime;
    }

    public void setIssueLastSyncTime(Date issueLastSyncTime) {
        this.issueLastSyncTime = issueLastSyncTime;
    }

    public String getIssueCreator() {
        return issueCreator;
    }

    public void setIssueCreator(String issueCreator) {
        this.issueCreator = issueCreator;
    }

    public String getIssueUpdateUser() {
        return issueUpdateUser;
    }

    public void setIssueUpdateUser(String issueUpdateUser) {
        this.issueUpdateUser = issueUpdateUser;
    }

    public String getIssuePersonIncharge() {
        return issuePersonIncharge;
    }

    public void setIssuePersonIncharge(String issuePersonIncharge) {
        this.issuePersonIncharge = issuePersonIncharge;
    }

    public String getIssueSyncUser() {
        return issueSyncUser;
    }

    public void setIssueSyncUser(String issueSyncUser) {
        this.issueSyncUser = issueSyncUser;
    }

    public Date getFcd() {
        return fcd;
    }

    public void setFcd(Date fcd) {
        this.fcd = fcd;
    }

    public String getFcu() {
        return fcu;
    }

    public void setFcu(String fcu) {
        this.fcu = fcu;
    }

    public Date getLcd() {
        return lcd;
    }

    public void setLcd(Date lcd) {
        this.lcd = lcd;
    }

    public String getLcu() {
        return lcu;
    }

    public void setLcu(String lcu) {
        this.lcu = lcu;
    }


    @Override
    public String toString() {
        return "SyncIssueVo{" +
                "id=" + id +
                ", sourceId=" + sourceId +
                ", source='" + source + '\'' +
                ", no='" + no + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isValid=" + isValid +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", handleUserId='" + handleUserId + '\'' +
                ", issueCreateTime=" + issueCreateTime +
                ", issueUpdateTime=" + issueUpdateTime +
                ", issueLastSyncTime=" + issueLastSyncTime +
                ", issueCreator='" + issueCreator + '\'' +
                ", issueUpdateUser='" + issueUpdateUser + '\'' +
                ", issuePersonIncharge='" + issuePersonIncharge + '\'' +
                ", issueSyncUser='" + issueSyncUser + '\'' +
                ", fcd=" + fcd +
                ", fcu='" + fcu + '\'' +
                ", lcd=" + lcd +
                ", lcu='" + lcu + '\'' +
                '}';
    }
}