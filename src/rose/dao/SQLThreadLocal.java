package rose.dao;

import java.util.Map;

import rose.dao.annotation.SQLType;
import rose.dao.provider.DaoDescription;

public class SQLThreadLocal {
    private static final ThreadLocal<SQLThreadLocal> locals = new ThreadLocal<SQLThreadLocal>();

    public static SQLThreadLocal get() {
        return locals.get();
    }

    public static SQLThreadLocal set(SQLType sqlType, String sql, DaoDescription desc,
            Map<String, ?> parameters) {
        SQLThreadLocal local = new SQLThreadLocal(sqlType, sql, desc, parameters);
        locals.set(local);
        return local;
    }

    public static void remove() {
        locals.remove();
    }

    private SQLType sqlType;

    private String sql;

    private DaoDescription desc;

    private Map<String, ?> parameters;

    private SQLThreadLocal(SQLType sqlType, String sql, DaoDescription desc, Map<String, ?> parameters) {
        this.sqlType = sqlType;
        this.sql = sql;
        this.desc = desc;
        this.parameters = parameters;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public boolean isReadType() {
        return this.sqlType == SQLType.READ;
    }

    public boolean isWriteType() {
        return this.sqlType == SQLType.WRITE;
    }

    public String getSql() {
        return sql;
    }

    public DaoDescription getDesc() {
        return desc;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }
}
