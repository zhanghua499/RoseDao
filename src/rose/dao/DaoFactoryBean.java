package rose.dao;

import java.lang.reflect.Proxy;

import rose.dao.provider.DaoProvider;

import rose.dao.DaoInvocationHandler;
import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

public class DaoFactoryBean<T> implements FactoryBean, InitializingBean {

	private T dao;
	
	private Class<T> daoClass;
	
	private DaoProvider daoProvider;
	
	public Object getObject() throws Exception {
        if (dao == null) {
            synchronized (this) {
                if (dao == null) {
                    dao = createDAO(daoClass);
                }
            }
        }
        return dao;
	}

	public Class<T> getObjectType() {
		return daoClass;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {

	}
	////////////////////////////////////help////////////////////////////////////////////
    @SuppressWarnings("unchecked")
	protected T createDAO(Class<T> daoClass) {
    	DaoDescription desc = new DaoDescription(daoClass);
        Dao dao = daoProvider.createDataAccess(daoClass);
        DaoInvocationHandler handler = new DaoInvocationHandler(desc, dao);
        return (T) Proxy.newProxyInstance
        (ClassUtils.getDefaultClassLoader(),new Class[] { daoClass }, handler);
    }
	
	/////////////////////////////////set////////////////////////////////////
	
    public void setDaoClass(Class<T> daoClass) {
        this.daoClass = daoClass;
    }
    
    public void setDaoProvider(DaoProvider daoProvider) {
        this.daoProvider = daoProvider;
    }
}
