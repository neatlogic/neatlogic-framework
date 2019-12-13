package codedriver.framework.counter.service;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.counter.core.GlobalCounterFactory;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.counter.dto.GlobalCounterSubscribeVo;
import codedriver.framework.counter.dto.GlobalCounterUserSortVo;
import codedriver.framework.counter.dto.GlobalCounterVo;
import codedriver.framework.counter.dao.mapper.GlobalCounterMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: balantflow
 * @description:
 * @create: 2019-09-10 16:31
 **/
@Service
@Transactional
public class GlobalCounterServiceImpl implements GlobalCounterService {

    @Autowired
    private GlobalCounterMapper counterMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    @Override
    public List<GlobalCounterVo> searchCounterVo(GlobalCounterVo counterVo) {
        boolean moduleId = StringUtils.isNotBlank(counterVo.getModuleId());
        List<GlobalCounterVo> activeCounterList = new ArrayList<>();
        List<GlobalCounterVo> counterVoList = GlobalCounterFactory.getCounterVoList();
        Map<String, ModuleVo> moduleVoMap = TenantContext.get().getActiveModuleMap();
        for (GlobalCounterVo c : counterVoList){
            if (moduleVoMap.containsKey(c.getModuleId())){
                if (!moduleId || (moduleId && c.getModuleId().equals(counterVo.getModuleId()))){
                    activeCounterList.add(c);
                }
            }
        }
        List<GlobalCounterUserSortVo> userCountSortList = counterMapper.getCounterSortListByUserId(UserContext.get().getUserId());
        Map<String, GlobalCounterUserSortVo> counterSortMap = new HashMap<>();
        for (GlobalCounterUserSortVo gus : userCountSortList){
            counterSortMap.put(gus.getPluginId(), gus);
        }

        List<GlobalCounterSubscribeVo> countSubList = counterMapper.getCounterSubscribeByUserId(UserContext.get().getUserId());
        Map<String, GlobalCounterSubscribeVo> subscribeMap = new HashMap<>();
        for (GlobalCounterSubscribeVo subscribeVo : countSubList){
            subscribeMap.put(subscribeVo.getPluginId(), subscribeVo);
        }


        for (GlobalCounterVo counter : activeCounterList){
            if (counterSortMap.containsKey(counter.getPluginId())){
                counter.setSort(counterSortMap.get(counter.getPluginId()).getSort());
            }
            if (subscribeMap.containsKey(counter.getPluginId())){
                counter.setCounterSubscribeVo(subscribeMap.get(counter.getPluginId()));
            }
        }
        return activeCounterList;
    }

    @Override
    public List<ModuleVo> getActiveCounterModuleList() {
        List<ModuleVo> moduleList = new ArrayList<>();
        List<ModuleVo> tenantModuleList = TenantContext.get().getActiveModuleList();
        List<GlobalCounterVo> counterList = GlobalCounterFactory.getCounterVoList();
        for (ModuleVo moduleVo : tenantModuleList){
            for (GlobalCounterVo counterVo : counterList){
                if (moduleVo.getId().equals(counterVo.getModuleId())){
                    moduleList.add(moduleVo);
                    break;
                }
            }
        }
        return moduleList;
    }

    @Override
    public List<GlobalCounterVo> getSubscribeCounterListByUserId(String userId) {
        List<GlobalCounterVo> subCounterVoList = counterMapper.getSubscribeCounterListByUserId(userId);
        List<GlobalCounterVo> counterVoList = GlobalCounterFactory.getCounterVoList();
        for (GlobalCounterVo subCounterVo : subCounterVoList){
            for (GlobalCounterVo counterVo : counterVoList){
                if (subCounterVo.getPluginId().equals(counterVo.getPluginId())){
                    subCounterVo.setModuleId(counterVo.getModuleId());
                }
            }
        }

        Map<String, ModuleVo> tenantModuleMap = TenantContext.get().getActiveModuleMap();
        if (subCounterVoList != null && subCounterVoList.size() > 0){
            for (GlobalCounterVo counterVo : subCounterVoList){
                ModuleVo moduleVo = tenantModuleMap.get(counterVo.getModuleId());
                counterVo.setModuleName(moduleVo.getName());
                counterVo.setModuleDesc(moduleVo.getDescription());
            }
        }
        return subCounterVoList;
    }

    @Override
    public void updateCounterSubscribe(GlobalCounterSubscribeVo counterSubscribeVo) {
        if (counterSubscribeVo.getId() != null && counterSubscribeVo.getId() != 0L){
            counterMapper.deleteCounterSubscribe(counterSubscribeVo.getId());
        }else {
            counterMapper.insertCounterSubscribe(counterSubscribeVo);
        }
    }

    @Override
    public void updateCounterUserSort(String userId, String sortPluginIdStr) {
        if (!StringUtils.isBlank(sortPluginIdStr)){
            counterMapper.deleteCounterUserSortByUserId(userId);
            String[] pluginIdArray = sortPluginIdStr.split(",");
            for (int i = 0; i < pluginIdArray.length; i++){
                GlobalCounterUserSortVo userSortVo = new GlobalCounterUserSortVo();
                userSortVo.setPluginId(pluginIdArray[i]);
                userSortVo.setSort(i);
                userSortVo.setUserId(userId);
                counterMapper.insertCounterUserSort(userSortVo);
            }
        }
    }
}
