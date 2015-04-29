package rose.dao.provider.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import rose.dao.interpreter.SQLInterpreter;

import rose.dao.annotation.SQLType;
import rose.dao.SQLThreadLocal;
import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;
import rose.dao.interpreter.SQLInterpreterResult;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.Assert;

public class SpringJdbcTemplateDao implements Dao {

	private SQLInterpreter[] interpreters = new SQLInterpreter[0];
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	
	public SpringJdbcTemplateDao() {
    }

	public SpringJdbcTemplateDao(DataSource dataSource) {
        jdbcTemplate.setDataSource(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate.setDataSource(dataSource);
    }

    public void setInterpreters(SQLInterpreter[] interpreters) {
        this.interpreters = interpreters;
    }
	
	public int[] batchUpdate(String sql, DaoDescription desc,
			List<Map<String, Object>> parametersList) {
        int[] updated = new int[parametersList.size()];
        for (int i = 0; i < updated.length; i++) {
            Map<String, Object> parameters = parametersList.get(i);
            SQLThreadLocal.set(SQLType.WRITE, sql, desc, parameters);
            updated[i] = update(sql, desc, parameters);
            SQLThreadLocal.remove();
        }
        return updated;
	}

	public Number insertReturnId(String sql, DaoDescription desc,
			Map<String, Object> parameters) {
        String sqlString = sql;
        Object[] arrayParameters = null;
        SQLInterpreterResult ir = null;
        for (SQLInterpreter interpreter : interpreters) {
            ir = interpreter.interpret(jdbcTemplate.getDataSource(), sqlString, desc,
                    parameters, arrayParameters);
            if (ir != null) {
                sqlString = ir.getSQL();
                arrayParameters = ir.getParameters();
            }
        }
        return insertReturnIdByJdbcTemplate(sqlString, arrayParameters);
	}

	public List<?> select(String sql, DaoDescription desc,
			Map<String, Object> parameters, RowMapper rowMapper) {
        String sqlString = sql;
        Object[] arrayParameters = null;
        SQLInterpreterResult ir = null;
        for (SQLInterpreter interpreter : interpreters) {
            ir = interpreter.interpret(jdbcTemplate.getDataSource(), sqlString, desc,
                    parameters, arrayParameters);
            if (ir != null) {
                sqlString = ir.getSQL();
                arrayParameters = ir.getParameters();
            }
        }
        return selectByJdbcTemplate(sqlString, arrayParameters, rowMapper);
	}

	public int update(String sql, DaoDescription desc,
			Map<String, Object> parameters) {
        String sqlString = sql;
        Object[] arrayParameters = null;
        SQLInterpreterResult ir = null;
        for (SQLInterpreter interpreter : interpreters) {
            ir = interpreter.interpret(jdbcTemplate.getDataSource(), sqlString, desc,
                    parameters, arrayParameters);
            if (ir != null) {
                sqlString = ir.getSQL();
                arrayParameters = ir.getParameters();
            }
        }
        return updateByJdbcTemplate(sqlString, arrayParameters);
	}

	///////////////////////////help mehtod///////////////////////////////
    protected List<?> selectByJdbcTemplate(String sql, Object[] parameters, RowMapper rowMapper) {

        if (parameters != null && parameters.length > 0) {

            return jdbcTemplate.query(sql, parameters, rowMapper);

        } else {

            return jdbcTemplate.query(sql, rowMapper);
        }
    }
    
    protected int updateByJdbcTemplate(String sql, Object[] parameters) {

        if (parameters != null && parameters.length > 0) {

            return jdbcTemplate.update(sql, parameters);

        } else {

            return jdbcTemplate.update(sql);
        }
    }
    
    
    protected void batchUpdateByJdbcTemplate(Map<String, List<Object[]>> ps, int[] updated,
            Map<String, int[]> positions) {
        for (Map.Entry<String, List<Object[]>> batch : ps.entrySet()) {
            String sql = batch.getKey();
            final List<Object[]> parametersList = batch.getValue();
            int[] subUpdated = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                public int getBatchSize() {
                    return parametersList.size();
                }
                
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Object[] args = parametersList.get(i);
                    for (int j = 0; j < args.length; j++) {
                        Object arg = args[j];
                        if (arg instanceof SqlParameterValue) {
                            SqlParameterValue paramValue = (SqlParameterValue) arg;
                            StatementCreatorUtils.setParameterValue(ps, j + 1, paramValue,
                                    paramValue.getValue());
                        } else {
                            StatementCreatorUtils.setParameterValue(ps, j + 1,
                                    SqlTypeValue.TYPE_UNKNOWN, arg);
                        }
                    }
                }
            });

            int[] subPositions = positions.get(sql);
            for (int i = 0; i < subUpdated.length; i++) {
                updated[subPositions[i]] = subUpdated[i];
            }
        }

    }
    
    protected Number insertReturnIdByJdbcTemplate(String sql, Object[] parameters) {

        if (parameters != null && parameters.length > 0) {

        	PreparedStatementCallbackReturnPK callbackReturnId = new PreparedStatementCallbackReturnPK(
                    new ArgPreparedStatementSetter(parameters));

            return (Number) jdbcTemplate.execute(new GenerateKeysPreparedStatementCreator(sql),
                    callbackReturnId);

        } else {

        	PreparedStatementCallbackReturnPK callbackReturnId = new PreparedStatementCallbackReturnPK();

            return (Number) jdbcTemplate.execute(new GenerateKeysPreparedStatementCreator(sql),
                    callbackReturnId);
        }
    }

    // 创建  PreparedStatement 时指定  Statement.RETURN_GENERATED_KEYS 属性
    private static class GenerateKeysPreparedStatementCreator implements PreparedStatementCreator,
            SqlProvider {

        private final String sql;

        public GenerateKeysPreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
        }

        public String getSql() {
            return this.sql;
        }
    }
}
