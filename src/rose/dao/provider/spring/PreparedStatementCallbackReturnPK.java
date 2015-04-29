package rose.dao.provider.spring;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

public class PreparedStatementCallbackReturnPK implements PreparedStatementCallback {
    private PreparedStatementSetter setter;

    public PreparedStatementCallbackReturnPK() {
    }

    public PreparedStatementCallbackReturnPK(PreparedStatementSetter setter) {
        this.setter = setter;
    }

    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException,
            DataAccessException {

        if (setter != null) {
            setter.setValues(ps);
        }

        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys != null) {

            try {
                RowMapperResultSetExtractor extractor = new RowMapperResultSetExtractor(
                        new SingleColumnRowMapper(Number.class), 1);
                return DataAccessUtils.requiredSingleResult((List<?>) extractor.extractData(keys));
            } finally {
                JdbcUtils.closeResultSet(keys);
            }
        }

        return null;
    }
}
