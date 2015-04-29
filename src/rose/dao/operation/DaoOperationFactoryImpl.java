package rose.dao.operation;

import java.util.regex.Pattern;

import rose.dao.annotation.SQL;
import rose.dao.annotation.SQLType;
import rose.dao.DaoOperation;
import rose.dao.RowMapperFactory;
import rose.dao.RowMapperFactoryImpl;
import rose.dao.operation.ReadOperation;
import rose.dao.operation.WriteOperation;

import org.springframework.jdbc.core.RowMapper;

import rose.dao.DaoOperationFactory;
import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

public class DaoOperationFactoryImpl implements DaoOperationFactory {

	   private static Pattern[] SELECT_PATTERNS = new Pattern[] {
		    //
		            Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE), //
		            Pattern.compile("^\\s*SHOW\\s+", Pattern.CASE_INSENSITIVE), //
		            Pattern.compile("^\\s*DESC\\s+", Pattern.CASE_INSENSITIVE), //
		    };

		    private RowMapperFactory rowMapperFactory = new RowMapperFactoryImpl();

		    public DaoOperation getDaoOperation(Dao dataAccess, DaoDescription desc) {

		        // 检查方法的  Annotation
		        SQL sql = desc.getAnnotation(SQL.class);

		        String sqlString = sql.value();
		        SQLType sqlType = sql.type();
		        if (sqlType == SQLType.UN_KNOW) {
		            for (int i = 0; i < SELECT_PATTERNS.length; i++) {
		                // 用正则表达式匹配  SELECT 语句
		                if (SELECT_PATTERNS[i].matcher(sqlString).find()) {
		                    sqlType = SQLType.READ;
		                    break;
		                }
		            }
		            if (sqlType == SQLType.UN_KNOW) {
		                sqlType = SQLType.WRITE;
		            }
		        }

		        //
		        if (SQLType.READ == sqlType) {
		            // 获得  RowMapper
		            RowMapper rowMapper = rowMapperFactory.getRowMapper(desc);
		            // SELECT 查询
		            return new ReadOperation(dataAccess, sqlString, desc, rowMapper);

		        } else if (SQLType.WRITE == sqlType) {
		            // INSERT / UPDATE / DELETE 查询
		            return new WriteOperation(dataAccess, sqlString, desc);
		        }

		        // 抛出检查异常
		        throw new AssertionError("Unknown SQL type: " + sqlType);
		    }

}
