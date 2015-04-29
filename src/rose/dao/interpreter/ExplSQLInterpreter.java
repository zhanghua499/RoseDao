package rose.dao.interpreter;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import rose.expl.ExqlPattern;
import rose.expl.impl.ExqlContextImpl;
import rose.expl.impl.ExqlPatternImpl;

import org.springframework.core.annotation.Order;
import org.springframework.jdbc.BadSqlGrammarException;

import rose.dao.provider.DaoDescription;

@Order(1)
public class ExplSQLInterpreter implements SQLInterpreter {

	public SQLInterpreterResult interpret(DataSource dataSource, String sql,
			DaoDescription desc, Map<String, Object> parametersAsMap,
			Object[] parametersAsArray) {
		// 转换语句中的表达式
        ExqlPattern pattern = ExqlPatternImpl.compile(sql);
        SQLExqlContextImpl context = new SQLExqlContextImpl(sql.length() + 32);

        try {
            pattern.execute(context, parametersAsMap);

        } catch (Exception e) {
            String daoInfo = desc.toString();
            throw new BadSqlGrammarException(daoInfo, sql, new SQLException(daoInfo
                    + " @SQL('" + sql + "')"));
        }

        return context;
	}
	
	class SQLExqlContextImpl extends ExqlContextImpl implements SQLInterpreterResult{

		public SQLExqlContextImpl(int capacity) {
			super(capacity);
		}

		public Object[] getParameters() {
			return getParams();
		}

		public String getSQL() {
			return flushOut();
		}
		
	}

}
