package codedriver.framework.reminder.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.ReminderMessageVo;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMapper;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: balantflow
 * @description: 异步消息保存
 * @create: 2019-09-09 14:22
 **/
@Service
public class GlobalReminderMessageHandler {

    static Logger logger = LoggerFactory.getLogger(GlobalReminderMessageHandler.class);

    private static final ThreadLocal<List<Runnable>> RUNNABLES = new ThreadLocal<>();

    private static GlobalReminderMapper reminderMapper;

    private static GlobalReminderMessageMapper reminderMessageMapper;

    @Autowired
    private  void setReminderMapper(GlobalReminderMapper reminderMapper) {
        GlobalReminderMessageHandler.reminderMapper = reminderMapper;
    }

    @Autowired
    private  void setReminderMessageMapper(GlobalReminderMessageMapper reminderMessageMapper){
        GlobalReminderMessageHandler.reminderMessageMapper = reminderMessageMapper;
    }

    public synchronized static void sendMessage(ReminderMessageVo messageVo, String pluginId){
        String tenantUuid = TenantContext.get().getTenantUuid();
        GlobalReminderMessageHandler.MessageRunner runner = new GlobalReminderMessageHandler.MessageRunner(messageVo, pluginId, tenantUuid);
        if (!TransactionSynchronizationManager.isSynchronizationActive()){
            CommonThreadPool.execute(runner);
            return;
        }
        List<Runnable> runnables = RUNNABLES.get();
        if (runnables == null){
            runnables = new ArrayList<Runnable>();
            RUNNABLES.set(runnables);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    List<Runnable> runnables = RUNNABLES.get();
                    for (int i = 0; i < runnables.size(); i++){
                        Runnable runnable = runnables.get(i);
                        CommonThreadPool.execute(runnable);
                    }
                }

                @Override
                public void afterCompletion(int status){
                    RUNNABLES.remove();
                }
            });
        }
        runnables.add(runner);
    }

    static class MessageRunner implements Runnable{

        private ReminderMessageVo mess;

        private String pluginId;

        private String tenantUuid;

        public MessageRunner(ReminderMessageVo _mess, String _pluginId, String _tenantUuid){

            mess = _mess;
            pluginId = _pluginId;
            tenantUuid = _tenantUuid;
        }

        @Override
        public void run() {
            String oldName = Thread.currentThread().getName();
            Thread.currentThread().setName("SYSTEMREMIND-MESSAGEHANDLER-" + mess.getTitle());
            try{
                if (tenantUuid != null){
                    TenantContext.init(tenantUuid);
                }
                GlobalReminderMessageVo message = new GlobalReminderMessageVo();
                message.setTitle(mess.getTitle());
                message.setContent(mess.getContent());
                message.setFromUser(mess.getFromUser());
                message.setPluginId(pluginId);
                message.setParam(mess.getParamObj() == null ? "" : mess.getParamObj().toString());
                reminderMessageMapper.insertReminderMessage(message);
                reminderMessageMapper.insertReminderMessageContent(message);
                List<String> userIdList = new ArrayList<>();
                if (mess.getReceiveTeamList() != null && mess.getReceiveTeamList().size() > 0){
                    userIdList = reminderMapper.getUserIdListByTeamIdList(mess.getReceiveTeamList());
                }
                if (mess.getReceiverList() != null && mess.getReceiverList().size() > 0){
                    for (String userId : mess.getReceiverList()){
                        if (!userIdList.contains(userId)){
                            userIdList.add(userId);
                        }
                    }
                }
                //获取订阅者名单
                List<String> subUserIdList = reminderMapper.getSubscribeUserIdListByPluginId(message.getPluginId());
                subUserIdList.retainAll(userIdList);
                for (String userId : subUserIdList){
                    reminderMessageMapper.insertReminderMessageUser(message.getId(), userId);
                }

            }catch (Exception ex){
                logger.error(ex.getMessage(), ex);
            }
            finally {
                Thread.currentThread().setName(oldName);
            }
        }
    }
}
