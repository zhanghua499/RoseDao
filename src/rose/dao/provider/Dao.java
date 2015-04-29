package rose.dao.provider;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

public interface Dao {
	
	public List<?> select(String sql, DaoDescription desc, Map<String, Object> parameters,RowMapper rowMapper);

	public int update(String sql, DaoDescription desc, Map<String, Object> parameters);
	
	public int[] batchUpdate(String sql, DaoDescription desc, List<Map<String, Object>> parametersList);

    public Number insertReturnId(String sql, DaoDescription desc, Map<String, Object> parameters);
}
