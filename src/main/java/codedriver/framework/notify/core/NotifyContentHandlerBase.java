package codedriver.framework.notify.core;

import codedriver.framework.common.dto.ValueTextVo;
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

    protected abstract JSONArray getMyConditionOptionList();

    protected abstract JSONArray getMyMessageAttrList(String handler);

    protected abstract List<ValueTextVo> getMyDataColumnList();
}