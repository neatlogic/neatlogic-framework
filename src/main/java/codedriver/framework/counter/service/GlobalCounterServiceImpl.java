package codedriver.framework.counter.service;

import codedriver.framework.asynchronization.threadlocal.UserContext;
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
        return counterMapper.getCounterList(counterVo);
    }

    @Override
    public List<ModuleVo> getActiveCounterModuleList() {
        Map<String, ModuleVo> tenantModuleMap = new HashMap<>();
        List<ModuleVo> moduleList = new ArrayList<>();
        List<ModuleVo> activeModuleList = counterMapper.getActiveCounterModuleList();
        for (ModuleVo module : activeModuleList){
            moduleList.add(tenantModuleMap.get(module.getId()));

        }
        return moduleList;
    }

    @Override
    public List<GlobalCounterVo> getSubscribeCounterListByUserId(String userId) {
        List<GlobalCounterVo> counterVoList = counterMapper.getSubscribeCounterListByUserId(userId);
        String tenant = UserContext.get().getTenant();
       /* List<ModuleVo> tenantModuleList = ModuleUtil.getTenantActionModuleList(moduleMapper.getModuleListByTenantUuid(tenant));*/
        Map<String, ModuleVo> tenantModuleMap = new HashMap<>();
        if (counterVoList != null && counterVoList.size() > 0){
            for (GlobalCounterVo counterVo : counterVoList){
                ModuleVo moduleVo = tenantModuleMap.get(counterVo.getModuleId());
                counterVo.setModuleName(moduleVo.getName());
                counterVo.setDescription(moduleVo.getDescription());
            }
        }
        return counterVoList;
    }

    @Override
    public void updateCounterSubscribe(GlobalCounterSubscribeVo counterSubscribeVo) {
        if (counterSubscribeVo.getId() != null){
            counterMapper.deleteCounterSubscribe(counterSubscribeVo.getId());
        }else {
            counterMapper.insertCounterSubscribe(counterSubscribeVo);
        }
    }

    @Override
    public void updateCounterUserSort(String userId, String sortIdStr) {
        if (!StringUtils.isBlank(sortIdStr)){
            counterMapper.deleteCounterUserSortByUserId(userId);
            String[] counterIdArray = sortIdStr.split(",");
            for (int i = 0; i < counterIdArray.length; i++){
                GlobalCounterUserSortVo userSortVo = new GlobalCounterUserSortVo();
                userSortVo.setCounterId(Long.parseLong(counterIdArray[i]));
                userSortVo.setSort(i);
                userSortVo.setUserId(userId);
                counterMapper.insertCounterUserSort(userSortVo);
            }
        }
    }
}
