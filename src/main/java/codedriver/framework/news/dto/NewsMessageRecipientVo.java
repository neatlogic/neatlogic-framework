package codedriver.framework.news.dto;

/**
 * @Title: NewsMessageRecipientVo
 * @Package codedriver.framework.news.dto
 * @Description: 消息接收者Vo
 * @Author: linbq
 * @Date: 2020/12/30 16:33
 **/
public class NewsMessageRecipientVo {
    private Long newsMessageId;
    private String type;
    private String uuid;

    public Long getNewsMessageId() {
        return newsMessageId;
    }

    public void setNewsMessageId(Long newsMessageId) {
        this.newsMessageId = newsMessageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
