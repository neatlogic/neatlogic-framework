package codedriver.framework.notify.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.notify.dto.NotifyTreeVo;
import codedriver.framework.notify.dto.NotifyTriggerVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RootComponent
public class NotifyPolicyHandlerFactory extends ApplicationListenerBase {

    private static final List<ValueTextVo> notifyPolicyHandlerList = new ArrayList<>();

    private static final Map<String, INotifyPolicyHandler> notifyPolicyHandlerMap = new HashMap<>();

    private static final List<NotifyTreeVo> moduleTreeVoList = new ArrayList<>();

    private static final Map<String, NotifyTreeVo> moduleTreeVoMap = new HashMap<>();

	private static List<NotifyTreeVo> notifyPolicyTreeVoList = new ArrayList<>();

	private static Map<String, NotifyTreeVo> notifyPolicyGroupTreeVoMap = new HashMap<>();

	public static INotifyPolicyHandler getHandler(String handler) {
		return notifyPolicyHandlerMap.get(handler);
	}

	public static List<ValueTextVo> getNotifyPolicyHandlerList(){
		return notifyPolicyHandlerList;
	}

    public static List<NotifyTreeVo> getModuleTreeVoList() {
        return moduleTreeVoList;
    }

    public static List<String> getTriggerList(String type) {
        NotifyTreeVo targetNode = getTargetNode(moduleTreeVoList, type);
        return getTriggerList(targetNode);
    }

    /**
     * @Description: 遍历消息分类树找到用户选中的节点
     * @Author: linbq
     * @Date: 2021/2/23 10:19
     * @Params: [treeVoList, type]
     * @Returns: codedriver.framework.notify.dto.NotifyTreeVo
     **/
    private static NotifyTreeVo getTargetNode(List<NotifyTreeVo> treeVoList, String type) {
        if (CollectionUtils.isNotEmpty(treeVoList)) {
            for (NotifyTreeVo notifyTreeVo : treeVoList) {
                if (notifyTreeVo.getUuid().equals(type)) {
                    return notifyTreeVo;
                } else {
                    NotifyTreeVo targetNode = getTargetNode(notifyTreeVo.getChildren(), type);
                    if (targetNode != null) {
                        return targetNode;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @Description: 遍历选择节点的所有子节点，找到所有触发点集合
     * @Author: linbq
     * @Date: 2021/2/23 10:18
     * @Params: [notifyTreeVo]
     * @Returns: java.util.List<java.lang.String>
     **/
    private static List<String> getTriggerList(NotifyTreeVo notifyTreeVo) {
        List<String> resultList = new ArrayList<>();
        if (notifyTreeVo != null) {
            List<NotifyTreeVo> children = notifyTreeVo.getChildren();
            if (children == null) {
                resultList.add(notifyTreeVo.getUuid());
            } else {
                for (NotifyTreeVo child : children) {
                    resultList.addAll(getTriggerList(child));
                }
            }
        }
        return resultList;
    }
    public static List<NotifyTreeVo> getNotifyPolicyTreeVoList(){
        return notifyPolicyTreeVoList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, INotifyPolicyHandler> map = context.getBeansOfType(INotifyPolicyHandler.class);
        for (Entry<String, INotifyPolicyHandler> entry : map.entrySet()) {
            INotifyPolicyHandler notifyPolicyHandler = entry.getValue();
            notifyPolicyHandlerMap.put(notifyPolicyHandler.getClassName(), notifyPolicyHandler);
            notifyPolicyHandlerList.add(new ValueTextVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName()));

			NotifyTreeVo treeVo = new NotifyTreeVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName());
			List<NotifyTreeVo> children = new ArrayList<>();
			for(NotifyTriggerVo notifyTriggerVo : notifyPolicyHandler.getNotifyTriggerListForNotifyTree()){
				children.add(new NotifyTreeVo(notifyTriggerVo.getTrigger(), notifyTriggerVo.getTriggerName()));
			}
			treeVo.setChildren(children);
			ModuleVo moduleVo = ModuleUtil.getModuleById(context.getId());
			NotifyTreeVo parentTreeVo = moduleTreeVoMap.get(moduleVo.getGroup());
			if(parentTreeVo == null){
				parentTreeVo = new NotifyTreeVo(moduleVo.getGroup(), moduleVo.getGroupName());
				moduleTreeVoMap.put(moduleVo.getGroup(), parentTreeVo);
				moduleTreeVoList.add(parentTreeVo);
			}
			parentTreeVo.addChildren(treeVo);

			INotifyPolicyHandlerGroup  notifyPolicyHandlerGroup = notifyPolicyHandler.getGroup();
			if(notifyPolicyHandlerGroup == null){
				notifyPolicyTreeVoList.add(new NotifyTreeVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName()));
			}else {
				NotifyTreeVo notifyPolicyGroupTreeVo = notifyPolicyGroupTreeVoMap.get(notifyPolicyHandlerGroup.getValue());
				if(notifyPolicyGroupTreeVo == null){
					notifyPolicyGroupTreeVo = new NotifyTreeVo(notifyPolicyHandlerGroup.getValue(), notifyPolicyHandlerGroup.getText());
					notifyPolicyGroupTreeVoMap.put(notifyPolicyHandlerGroup.getValue(), notifyPolicyGroupTreeVo);
					notifyPolicyTreeVoList.add(notifyPolicyGroupTreeVo);
				}
				notifyPolicyGroupTreeVo.addChildren(new NotifyTreeVo(notifyPolicyHandler.getClassName(), notifyPolicyHandler.getName()));
			}
		}
	}

    @Override
    protected void myInit() {

    }

}
