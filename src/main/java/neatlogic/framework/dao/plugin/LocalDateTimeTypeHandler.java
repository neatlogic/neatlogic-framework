package neatlogic.framework.dao.plugin;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedTypes(LocalDateTime.class)
public class LocalDateTimeTypeHandler extends BaseTypeHandler<String> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, LocalDateTime.parse(parameter, formatter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        LocalDateTime localDateTime = rs.getObject(columnName, LocalDateTime.class);
        return localDateTime != null ? localDateTime.format(formatter) : null;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        LocalDateTime localDateTime = rs.getObject(columnIndex, LocalDateTime.class);
        return localDateTime != null ? localDateTime.format(formatter) : null;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        LocalDateTime localDateTime = cs.getObject(columnIndex, LocalDateTime.class);
        return localDateTime != null ? localDateTime.format(formatter) : null;
    }

}
