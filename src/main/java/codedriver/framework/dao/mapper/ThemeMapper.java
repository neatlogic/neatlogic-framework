package codedriver.framework.dao.mapper;

import codedriver.framework.dto.ThemeVo;

/**
 * @author longrf
 * @date 2022/4/8 4:02 下午
 */
public interface ThemeMapper {

    ThemeVo getTheme();

    void insertTheme(ThemeVo themeVo);

    void deleteTheme();

    ThemeVo getTenantTheme(String dbName);
}
