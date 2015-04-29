package rose.dao;

import java.util.List;
import java.util.Map;

import rose.dao.annotation.SQLType;
import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

import org.springframework.jdbc.core.RowMapper;

public class SQLThreadLocalWrapper extends DaoWrapper{
    public SQLThreadLocalWrapper() {
    }

    public SQLThreadLocalWrapper(Dao dao) {
        this.targetDao = dao;
    }


    public List<?> select(String sql, DaoDescription desc, Map<String, Object> parameters,
            RowMapper rowMapper) {
        SQLThreadLocal.set(SQLType.READ, sql, desc, parameters);
        try {
            return targetDao.select(sql, desc, parameters, rowMapper);
        } finally {
            SQLThreadLocal.remove();
        }
    }


    public int update(String sql, DaoDescription desc, Map<String, Object> parameters) {
        SQLThreadLocal.set(SQLType.WRITE, sql, desc, parameters);
        try {
            return targetDao.update(sql, desc, parameters);
        } finally {
            SQLThreadLocal.remove();
        }
    }


    public Number insertReturnId(String sql, DaoDescription desc, Map<String, Object> parameters) {
        SQLThreadLocal.set(SQLType.WRITE, sql, desc, parameters);
        try {
            return targetDao.insertReturnId(sql, desc, parameters);
        } finally {
            SQLThreadLocal.remove();
        }
    }
}
