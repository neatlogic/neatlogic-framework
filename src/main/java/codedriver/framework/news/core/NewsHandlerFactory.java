package codedriver.framework.news.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.news.constvalue.PopUpType;
import codedriver.framework.news.dto.NewsHandlerVo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: NewsHandlerFactory
 * @Package codedriver.framework.news.core
 * @Description: 消息处理器工厂类
 * @Author: linbq
 * @Date: 2020/12/30 15:36
 **/
@RootComponent
public class NewsHandlerFactory extends ApplicationListenerBase {
    private static Map<String, INewsHandler> newsHandlerMap = new HashMap<>();

    private static List<NewsHandlerVo> newsHandlerVoList = new ArrayList<>();

    @Override
    protected void myInit() {

    }

    public static INewsHandler getHandler(String handler){
        return newsHandlerMap.get(handler);
    }

    public static List<NewsHandlerVo> getNewsHandlerVoList(){
        return newsHandlerVoList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, INewsHandler> map = context.getBeansOfType(INewsHandler.class);
        for(Map.Entry<String, INewsHandler> entry : map.entrySet()){
            INewsHandler newshandler = entry.getValue();
            System.out.println("newshandler:"+newshandler);
            newsHandlerMap.put(newshandler.getHandler(), newshandler);

            NewsHandlerVo newsHandlerVo = new NewsHandlerVo();
            newsHandlerVo.setHandler(newshandler.getHandler());
            newsHandlerVo.setDescription(newshandler.getDescription());
            newsHandlerVo.setName(newshandler.getName());
            newsHandlerVo.setIsActive(1);
            newsHandlerVo.setModuleId(context.getId());
            ModuleVo moduleVo = ModuleUtil.getModuleById(context.getId());
            newsHandlerVo.setModuleName(moduleVo.getName());
            newsHandlerVo.setPopUp(PopUpType.CLOSE.getValue());
            newsHandlerVoList.add(newsHandlerVo);
        }
    }
}
