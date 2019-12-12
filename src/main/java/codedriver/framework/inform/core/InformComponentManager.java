package codedriver.framework.inform.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import codedriver.framework.inform.dto.EmailInformVo;
import codedriver.framework.inform.dto.EmailMessageVo;
import codedriver.framework.inform.dto.InformBaseVo;
import codedriver.framework.inform.dto.MessageBaseVo;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class InformComponentManager {
    Logger logger = LoggerFactory.getLogger(InformComponentManager.class);

    private static InformComponentManager manager;

    protected static UserMapper userMapper;

    @Autowired
    public  void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    public static void inform(InformBaseVo informVo){
        if (manager == null){
            manager = new InformComponentManager();
        }
        manager.executor(informVo);
    }

    public void executor(InformBaseVo informVo){
        String tenantUuid = TenantContext.get().getTenantUuid();
        CommonThreadPool.execute(new InformRunner(informVo, tenantUuid));
    }

    class InformRunner implements Runnable{
        private InformBaseVo informVo;
        private String tenantUuid;

        public InformRunner(InformBaseVo _informVo, String _tenantUuid){
            this.informVo = _informVo;
            this.tenantUuid = _tenantUuid;
        }

        @Override
        public void run() {
            if (TenantContext.get() == null){
                TenantContext.init(tenantUuid);
            }
            Thread.currentThread().setName(new StringBuilder("INFORM-").append(informVo.getPluginId()).toString());
            handler(informVo);
        }
    }

    private void handler(InformBaseVo informVo){
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
        MessageBaseVo message = new MessageBaseVo();

        //针对邮件提取抄送人,以此可扩展其他自定义属性
        if (informVo instanceof EmailInformVo){
            message = new EmailMessageVo();
            EmailInformVo emailInfo = (EmailInformVo) informVo;
            List<String> ccUserIdList =  emailInfo.getCcUserIdList();
            List<UserVo> ccUserList = new ArrayList<>();
            for (String ccUserId : ccUserIdList){
                UserVo userVo = userMapper.getUserByUserId(ccUserId);
                ccUserList.add(userVo);
            }
            ((EmailMessageVo) message).setCcUserList(ccUserList);
        }

        message.setParamObj(informVo.getParamObj());
        message.setFromUser(informVo.getFromUser());
        message.setToUserList(toUserList);
        message.setTitle(informVo.getTitle());
        message.setContent(informVo.getContent());
        if (StringUtils.isNotBlank(templateTitle)){
            message.setTitle(getFreemarkerContent(message, templateTitle));
        }
        if (StringUtils.isNotBlank(templateContent)){
            message.setContent(getFreemarkerContent(message, templateContent));
        }


        informPlugin.execute(message);
    }

    public  String getFreemarkerContent(MessageBaseVo messageVo, String content) {
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
