package neatlogic.framework.restful.core;

import neatlogic.framework.restful.dto.ApiExampleVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

/** 为 Object、Raw 和 SSE 接口提供可与注解合并的程序化场景声明。 */
public interface IApiExampleProvider {
    /** 返回按展示顺序排列的场景；标题和描述为 i18n key，无场景时返回空列表。 */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default List<ApiExampleVo> example() {
        return Collections.emptyList();
    }
}
