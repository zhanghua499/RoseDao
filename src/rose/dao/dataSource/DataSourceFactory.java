package rose.dao.dataSource;

import javax.sql.DataSource;

public interface DataSourceFactory {
	DataSource getDataSource(Class<?> daoClass);
}
