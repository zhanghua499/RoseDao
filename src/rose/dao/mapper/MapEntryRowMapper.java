package rose.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import rose.dao.annotation.KeyColumnOfMap;
import rose.dao.provider.DaoDescription;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

public class MapEntryRowMapper implements RowMapper {


    private final RowMapper mapper;

    private String keyColumn;

    private int keyColumnIndex = 1;

    private Class<?> keyType;

    private DaoDescription desc;

    public MapEntryRowMapper(DaoDescription desc, RowMapper mapper) {
        this.desc = desc;
        Class<?>[] genericTypes = desc.getGenericReturnTypes();
        if (genericTypes.length < 2) {
            throw new IllegalArgumentException("please set map generic parameters in method: "
                    + desc.getMethod());
        }
        KeyColumnOfMap mapKey = desc.getAnnotation(KeyColumnOfMap.class);
        this.keyColumn = (mapKey != null) ? mapKey.value() : null;
        this.keyType = genericTypes[0];
        this.mapper = mapper;
    }

    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rowNum == 0) {
            if (StringUtils.isNotEmpty(keyColumn)) {
                keyColumnIndex = rs.findColumn(keyColumn);
                if (keyColumnIndex <= 0) {
                    throw new IllegalArgumentException(String.format(
                            "wrong key name %s for method: %s ", keyColumn, desc.getMethod()));
                }
                keyColumn = null;
            }
        }

     // 从  JDBC ResultSet 获取 Key
        Object key = JdbcUtils.getResultSetValue(rs, keyColumnIndex, keyType);
        if (key != null && !keyType.isInstance(key)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            throw new TypeMismatchDataAccessException( // NL
                    "Type mismatch affecting row number " + rowNum + " and column type '"
                            + rsmd.getColumnTypeName(keyColumnIndex) + "' expected type is '"
                            + keyType + "'");
        }

        return new MapEntryImpl<Object, Object>(key, mapper.mapRow(rs, rowNum));
    }
}
