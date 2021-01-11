package codedriver.framework.notify.core;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyVo;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public abstract class NotifyContentHandlerBase implements INotifyContentHandler{
    @Override
    public JSONArray getConditionOptionList() {
        return getMyConditionOptionList();
    }

    @Override
    public JSONArray getMessageAttrList(String handler) {
        return getMyMessageAttrList(handler);
    }

    @Override
    public List<ValueTextVo> getDataColumnList() {
        return getMyDataColumnList();
    }

    @Override
    public List<NotifyVo> getNotifyData(Long id) {
        return getMyNotifyData(id);
    }

    @Override
    public String preview() {
        return myPreview();
    }

    protected abstract JSONArray getMyConditionOptionList();

    protected abstract JSONArray getMyMessageAttrList(String handler);

    protected abstract List<ValueTextVo> getMyDataColumnList();

    protected abstract List<NotifyVo> getMyNotifyData(Long id);

    protected abstract String myPreview();
}
