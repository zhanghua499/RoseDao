package rose.dao.provider.spring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import javax.sql.DataSource;

import rose.dao.dataSource.DataSourceFactory;
import rose.dao.provider.AbstractDaoProvider;
import rose.dao.provider.Dao;
import rose.dao.dataSource.SpringDataSourceFactory;
import rose.dao.interpreter.SQLInterpreter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

public class SpringJdbcTemplateDaoProvider extends AbstractDaoProvider implements ApplicationContextAware{

	protected ApplicationContext applicationContext;
	
	@Override
	protected Dao createDataAccess(DataSource dataSource) {
        SpringJdbcTemplateDao dataAccess = new SpringJdbcTemplateDao();
        dataAccess.setDataSource(dataSource);
        dataAccess.setInterpreters(findSQLInterpreters());
        return dataAccess;
	}

	@Override
	protected DataSourceFactory createDataSourceFactory() {
        Map<?, ?> beansOfType = applicationContext.getBeansOfType(DataSourceFactory.class);
        if (beansOfType.size() > 1) {
            throw new NoSuchBeanDefinitionException(DataSourceFactory.class,
                    "expected single bean but found " + beansOfType.size());
        } else if (beansOfType.size() == 1) {
            return (DataSourceFactory) beansOfType.values().iterator().next();
        }
        SpringDataSourceFactory dataSourceFactory = new SpringDataSourceFactory();
        dataSourceFactory.setApplicationContext(applicationContext);
        return dataSourceFactory;
	}

	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.applicationContext = ctx;
		
	}
	
    protected SQLInterpreter[] findSQLInterpreters() {
        @SuppressWarnings("unchecked")
        Collection<SQLInterpreter> interpreters = this.applicationContext.getBeansOfType(
                SQLInterpreter.class).values();
        SQLInterpreter[] arrayInterpreters = interpreters.toArray(new SQLInterpreter[0]);
        Arrays.sort(arrayInterpreters, new Comparator<SQLInterpreter>() {

            public int compare(SQLInterpreter thees, SQLInterpreter that) {
                Order thessOrder = thees.getClass().getAnnotation(Order.class);
                Order thatOrder = that.getClass().getAnnotation(Order.class);
                int thessValue = thessOrder == null ? 0 : thessOrder.value();
                int thatValue = thatOrder == null ? 0 : thatOrder.value();
                return thessValue - thatValue;
            }

        });
        return arrayInterpreters;
    }

}
