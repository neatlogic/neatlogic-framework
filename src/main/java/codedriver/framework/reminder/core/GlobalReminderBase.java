package codedriver.framework.reminder.core;

import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.ReminderMessageVo;
import codedriver.framework.reminder.dto.param.GlobalReminderParamVo;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMapper;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMessageMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: balantflow
 * @description: 系统通知--实时动态抽象类
 * @create: 2019-08-13 15:24
 **/

@Transactional
public abstract class GlobalReminderBase implements IGlobalReminder{

    static Logger logger = LoggerFactory.getLogger(GlobalReminderBase.class);

    protected static GlobalReminderMapper reminderMapper;

    protected static GlobalReminderMessageMapper reminderMessageMapper;

    @Autowired
    private  void setReminderMapper(GlobalReminderMapper _reminderMapper) {
        reminderMapper = _reminderMapper;
    }

    @Autowired
    private  void setReminderMessageMapper(GlobalReminderMessageMapper _reminderMessageMapper) {
        reminderMessageMapper = _reminderMessageMapper;
    }

   /** 
   * @Description: 消息订阅公共的参数控件（比如弹窗提醒）
   * @Param: [] 
   * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.param.GlobalReminderParamVo>  
   */ 
    @Override
    public List<GlobalReminderParamVo> getConfig() {
        List<GlobalReminderParamVo> paramVoList = new ArrayList<>();
        GlobalReminderParamVo popParam = new GlobalReminderParamVo();
        popParam.setShowName("弹框提醒");
        popParam.setName("popUp");
        popParam.setType(ControlEnum.RADIO.getValue());
        JSONArray popArray = new JSONArray();
        JSONObject pop1 = new JSONObject();
        pop1.put("key", "关闭");
        pop1.put("value", "close");
        popArray.add(pop1);
        JSONObject pop2 = new JSONObject();
        pop2.put("key", "临时");
        pop2.put("value", "temporary");
        popArray.add(pop2);
        JSONObject pop3 = new JSONObject();
        pop3.put("key", "持续");
        pop3.put("value", "continued");
        popArray.add(pop3);
        popParam.setOption(popArray);
        popParam.setDefaultValue("close");
        paramVoList.add(popParam);

        myConfig(paramVoList);

        return paramVoList;
    }

    /**
        * @Description: 处理公共数据
        * @Param: [messageVo]
        * @return: java.lang.Object */
    @Override
    public Object packData(GlobalReminderMessageVo messageVo) {
        JSONObject returnObj = new JSONObject();
        IGlobalReminder reminder = GlobalReminderFactory.getReminder(messageVo.getReminderVo().getPluginId());
        returnObj.put("id", messageVo.getId());
        returnObj.put("title", messageVo.getTitle());
        returnObj.put("content", messageVo.getContent());
        returnObj.put("createTime", messageVo.getCreateTime());
        returnObj.put("fromUserId", messageVo.getFromUser());
        returnObj.put("fromUserName", messageVo.getFromUserName());
        returnObj.put("remindName", messageVo.getReminderVo().getName());
        returnObj.put("moduleName", messageVo.getReminderVo().getModuleName());
        returnObj.put("moduleIcon", messageVo.getReminderVo().getModuleIcon());
        returnObj.put("receiver", messageVo.getReminderSubscribeVo().getUserId());
        returnObj.put("receiverName", messageVo.getReminderSubscribeVo().getUserName());
        returnObj.put("isKeep", messageVo.getIsKeep());
        returnObj.put("showTemplate", reminder.getShowTemplate());
        returnObj.put("popUpTemplate", reminder.getPopUpTemplate());
        String param = messageVo.getReminderSubscribeVo().getParam();
        boolean hasParam = param != null && !("").equals(param);
        //弹窗类型
        String popUpValue = "close";
        JSONObject paramJson = JSONObject.parseObject(param);
        if(hasParam&&paramJson!=null) {
        	popUpValue = paramJson.getString("popUp");
        }
        returnObj.put("popUp", popUpValue);
        //是否为新消息（新消息会触发一系列动作）
        returnObj.put("isNew", messageVo.getIsNew());
        if (messageVo.getIsNew() == 1){
            reminderMessageMapper.updateUserMessageNewStatus(messageVo.getId(), messageVo.getReminderSubscribeVo().getUserId());
        }
        //自定义信息参数，例：告警的级别
        String messageParam = messageVo.getParam();
        if (StringUtils.isNotBlank(messageParam)){
            returnObj.put("customData", messageParam);
        }

        //提取设置控件选项数据
        String controlDataStr = messageVo.getReminderSubscribeVo().getParam();
        JSONObject controlData = null;
        if (StringUtils.isNotBlank(controlDataStr)){
            controlData = JSON.parseObject(controlDataStr);
        }

        packMyData(returnObj, controlData);
        return returnObj;
    }

    @Override
    public void send(ReminderMessageVo message) {
        try{
            GlobalReminderMessageHandler.sendMessage(message,  ClassUtils.getUserClass(this.getClass()).getName());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
        * @Description: 自定义数据处理
        * @Param: []
        * @return: void  */
    public abstract  void packMyData(JSONObject resultObj, JSONObject controlData);

    /**
        * @Description: 各模块自行额外增加的参数控件（比如监控的声音设置）
        * @Param: [paramVoList]
        * @return: void*/
    public abstract void myConfig(List<GlobalReminderParamVo> paramVoList);

    /**
     * @Description: 弹窗面板路径
     * @Param: []
     * @return: java.lang.String
     */
    public abstract String getPopUpTemplate();
}
