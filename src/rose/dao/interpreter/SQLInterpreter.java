package rose.dao.interpreter;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.core.annotation.Order;

import rose.dao.provider.DaoDescription;

@Order(0)
public interface SQLInterpreter {
    SQLInterpreterResult interpret(DataSource dataSource, String sql, DaoDescription desc,
            Map<String, Object> parametersAsMap, Object[] parametersAsArray);
}
