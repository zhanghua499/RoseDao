package rose.dao;

import rose.dao.annotation.DAO;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import rose.dao.provider.Dao;
import rose.dao.provider.DaoProvider;
import rose.scan.SpringComponentSearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DaoPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware{

	private ApplicationContext applicationContext;
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf)
			throws BeansException {
		SpringComponentSearch cs = new SpringComponentSearch("dao","Dao");
		cs.addIncludeFilters(new AnnotationTypeFilter(DAO.class));
		cs.searchComponents();
		ArrayList<Object> dfs = cs.getComponents();
		Set<String> daoClassNames = new HashSet<String>();
		
		for(Object bdf : dfs){
			BeanDefinition beanDefinition = (BeanDefinition)bdf;
			String daoClassName = beanDefinition.getBeanClassName();
			if (daoClassNames.contains(daoClassName)) {
				continue;
			}
			daoClassNames.add(daoClassName);
			MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
			propertyValues.addPropertyValue("daoClass", daoClassName);
			propertyValues.addPropertyValue("daoProvider", new DaoProvider() {

	            final DaoProvider daoProvider = createDaoProvider();

	            public Dao createDataAccess(Class<?> daoClass) {
	                Dao dataAccess = daoProvider.createDataAccess(daoClass);
	                dataAccess = new SQLThreadLocalWrapper(dataAccess);
	                return dataAccess;
	            }
	        });
			ScannedGenericBeanDefinition scannedBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
			scannedBeanDefinition.setPropertyValues(propertyValues);
			scannedBeanDefinition.setBeanClass(DaoFactoryBean.class);
			
            DefaultListableBeanFactory defaultBeanFactory = (DefaultListableBeanFactory) bf;
            defaultBeanFactory.registerBeanDefinition(daoClassName, beanDefinition);
            System.out.println("register dao bean: " + daoClassName);
		}
	}

	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.applicationContext = ctx;
		
	}
	
    protected DaoProvider createDaoProvider() {
        return (DaoProvider) applicationContext.getBean("daoProvider",DaoProvider.class);
    }

}
