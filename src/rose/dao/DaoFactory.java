package rose.dao;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

public class DaoFactory{

	private ApplicationContext applicationContext;
	
	public DaoFactory(ApplicationContext ctx){
		this.applicationContext = ctx;
		
	}
    @SuppressWarnings("unchecked")
    public <T> T getDao(Class<T> daoClass) {
        return (T) BeanFactoryUtils.beanOfType(applicationContext, daoClass);
    }
}
