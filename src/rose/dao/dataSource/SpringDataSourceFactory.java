package rose.dao.dataSource;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringDataSourceFactory implements DataSourceFactory, ApplicationContextAware{

	private ApplicationContext applicationContext;
	private final String defaultDataSource = "dataSource";
	
	//传入daoClass是考虑将来做多数据源扩展用，现在没有用
	public DataSource getDataSource(Class<?> daoClass) {

		return getDataSource(defaultDataSource);
	}

	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.applicationContext = ctx;
		
	}

	private DataSource getDataSource(String key){
		if (applicationContext.containsBean(key)) {
			return (DataSource) applicationContext.getBean(key, DataSource.class);
		}
		 throw new IllegalArgumentException("DataSource:"+key+" not found!Please config it in Spring Context!");
	}
}
