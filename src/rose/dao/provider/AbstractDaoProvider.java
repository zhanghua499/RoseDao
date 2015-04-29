package rose.dao.provider;

import javax.sql.DataSource;

import rose.dao.dataSource.DataSourceFactory;

public abstract class AbstractDaoProvider implements DaoProvider{
    protected DataSourceFactory dataSourceFactory;

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public Dao createDataAccess(Class<?> daoClass) {

        if (dataSourceFactory == null) {
            dataSourceFactory = createDataSourceFactory();
        }

        DataSource dataSource = dataSourceFactory.getDataSource(daoClass);
        if (dataSource == null) {
            throw new NullPointerException("not found dataSource for dao: '" + daoClass.getName()
                    + "'.");
        }

        return createDataAccess(dataSource);
    }

    protected abstract Dao createDataAccess(DataSource dataSource);


    protected abstract DataSourceFactory createDataSourceFactory();
}
