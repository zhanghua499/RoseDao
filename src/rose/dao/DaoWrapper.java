package rose.dao;

import java.util.List;
import java.util.Map;

import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

import org.springframework.jdbc.core.RowMapper;

public class DaoWrapper implements Dao{
    protected Dao targetDao;

    public DaoWrapper() {
    }

    public DaoWrapper(Dao Dao) {
        this.targetDao = Dao;
    }

    public void setDao(Dao Dao) {
        this.targetDao = Dao;
    }


    public List<?> select(String sql, DaoDescription desc, Map<String, Object> parameters,
            RowMapper rowMapper) {
        return targetDao.select(sql, desc, parameters, rowMapper);
    }


    public int update(String sql, DaoDescription desc, Map<String, Object> parameters) {
        return targetDao.update(sql, desc, parameters);
    }


    public Number insertReturnId(String sql, DaoDescription desc, Map<String, Object> parameters) {
        return targetDao.insertReturnId(sql, desc, parameters);
    }


    public int[] batchUpdate(String sql, DaoDescription desc, List<Map<String, Object>> parametersList) {
        return targetDao.batchUpdate(sql, desc, parametersList);
    }
}
