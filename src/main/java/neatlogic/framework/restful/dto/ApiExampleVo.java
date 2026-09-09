package neatlogic.framework.restful.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

/** 程序化接口示例；说明保存 i18n key，请求仅接受 JSONObject 或 JSONArray。 */
public class ApiExampleVo {
    @EntityField(name = "common.title", type = ApiParamType.STRING)
    private String title;
    @EntityField(name = "common.description", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "common.example", type = ApiParamType.NOAUTH)
    private Object example;

    /** 支持逐字段组装，完整性在统一帮助解析阶段校验。 */
    public ApiExampleVo() { }

    /** 声明无需补充说明的场景。 */
    public ApiExampleVo(String title, Object example) {
        this(title, "", example);
    }

    /** 声明标题、适用条件及请求内容，翻译由统一解析器完成。 */
    public ApiExampleVo(String title, String description, Object example) {
        this.title = title;
        this.description = description;
        this.example = example;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Object getExample() { return example; }
    public void setExample(Object example) { this.example = example; }
}
