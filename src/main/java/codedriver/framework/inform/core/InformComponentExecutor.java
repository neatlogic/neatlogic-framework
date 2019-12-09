package codedriver.framework.inform.core;

import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import codedriver.framework.inform.dto.InformVo;
import codedriver.framework.inform.dto.MessageVo;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 10:45
 **/
public class InformComponentExecutor {
    Logger logger = LoggerFactory.getLogger(InformComponentExecutor.class);

    private InformVo informVo;

    @Autowired
    private UserMapper userMapper;

    public InformComponentExecutor(InformVo informVo){
        this.informVo = informVo;
    }

    public static void inform(InformVo informVo){
        InformComponentExecutor executor = new InformComponentExecutor(informVo);
        executor.executor();
    }

    public void executor(){
        CommonThreadPool.getThreadPool().execute(new InformRunner(informVo));
    }

    class InformRunner implements Runnable{
        private InformVo informVo;

        public InformRunner(InformVo _informVo){
            this.informVo = _informVo;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(new StringBuilder("INFORM-").append(informVo.getPluginId()).toString());
            handler(informVo);
        }
    }

    private void handler(InformVo informVo){
        InformComponentBase informPlugin = InformComponentFactory.getInformPlugin(informVo.getPluginId());
        String templateContent = informPlugin.getTemplateContent();
        String templateTitle = informPlugin.getTemplateTitle();
        if (StringUtils.isNotBlank(informVo.getTemplateContent())){
            templateContent = informVo.getTemplateContent();
        }
        if (StringUtils.isNotBlank(informVo.getTemplateTitle())){
            templateTitle = informVo.getTemplateTitle();
        }
        List<String> userIdList = informVo.getToUserIdList();
        List<UserVo> userList = new ArrayList<>();
        if (userIdList != null && userIdList.size() > 0){
            for (String userId : userIdList){
                UserVo userVo = userMapper.getUserByUserId(userId);
                userList.add(userVo);
            }
        }
        List<String> teamIdList = informVo.getToTeamIdList();
        if (teamIdList != null && teamIdList.size() > 0){
            for (String teamId : teamIdList){
               List<UserVo> userVoList = userMapper.getActiveUserByTeamId(teamId);
                userList.addAll(userVoList);
            }
        }
        Set<String> checkSet = new HashSet<>();
        List<UserVo> toUserList = new ArrayList<>();
        for (UserVo userVo : userList){
            if (!checkSet.contains(userVo.getUserId())){
                toUserList.add(userVo);
                checkSet.add(userVo.getUserId());
            }
        }
        MessageVo message = new MessageVo();
        message.setParamObj(informVo.getParamObj());
        message.setFromUser(informVo.getFromUser());
        message.setToUserList(toUserList);
        message.setTitle(getFreemarkerContent(message, templateTitle));
        message.setContent(getFreemarkerContent(message, templateContent));
        informPlugin.execute(message);
    }

    public  String getFreemarkerContent(MessageVo messageVo, String content) {
        String resultStr = "";
        if (content != null) {
            Configuration cfg = new Configuration();
            cfg.setNumberFormat("0.##");
            cfg.setClassicCompatible(true);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate("template", content);
            cfg.setTemplateLoader(stringLoader);
            Template temp;
            Writer out = null;
            try {
                temp = cfg.getTemplate("template", "utf-8");
                out = new StringWriter();
                temp.process(messageVo, out);
                resultStr = out.toString();
                out.flush();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (TemplateException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultStr;
    }
}
