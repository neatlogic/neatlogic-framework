package codedriver.framework.notify.handler;

import codedriver.framework.news.core.INewsHandler;
import codedriver.framework.news.core.NewsHandlerFactory;
import codedriver.framework.news.dto.NewsMessageVo;
import codedriver.framework.notify.core.NotifyHandlerBase;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.notify.dto.NotifyVo;

/**
 * @Title: NewsNotifyHandler
 * @Package codedriver.framework.notify.handler
 * @Description: 消息通知处理器
 * @Author: linbq
 * @Date: 2020/12/31 15:19
 **/
//@Component
public class NewsNotifyHandler extends NotifyHandlerBase {

//    private static Logger logger = LoggerFactory.getLogger(RemindNotifyHandler.class);

    @Override
    protected void myExecute(NotifyVo notifyVo) {
        this.sendRemind(notifyVo);
    }

    private void sendRemind(NotifyVo notifyVo){
        if (notifyVo.getToUserList().size() > 0){
            NewsMessageVo message = new NewsMessageVo();
            message.setTitle(notifyVo.getTitle());
            message.setContent(notifyVo.getContent());
//            message.setRecipientList();
            INewsHandler handler = NewsHandlerFactory.getHandler("ProcessTaskRemindHandler.class.getName()");//TODO linbq
            handler.send(message);
        }
    }

    @Override
    public String getName() {
        return NotifyHandlerType.REMIND.getText();
    }

	@Override
	public String getType() {
		return NotifyHandlerType.REMIND.getValue();
	}
}
