package rose.scan;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class SpringComponentSearch extends ComponentSearch{

	protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	
	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
	
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	
    private List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

    private List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
            this.resourcePatternResolver);
	
	public SpringComponentSearch(){
		//默认扫描控制器
		this("action","Action");
		
	}
	public SpringComponentSearch(String componentName,String componentSuffix){
		this.componentName = componentName;
		this.componentSuffix = componentSuffix;
		//searchComponents();
	}
	public void searchComponents() {	
		//取得类加载器集合	
		try {
			Enumeration<URL> founds = this.resourcePatternResolver.getClassLoader().getResources("");
			while (founds.hasMoreElements()) {
				URL urlObject = founds.nextElement();
				findComponents(urlObject.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void findComponents(String uriPrefix) {
        if (!uriPrefix.endsWith("/")) {
            uriPrefix = uriPrefix + "/";
        }
        try {
            String packageSearchPath = uriPrefix + this.resourcePattern;
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];

                if (!resource.exists()) {
                	//......................
                } else if (resource.isReadable()) {
                    if (!resource.getFilename().endsWith(componentSuffix+".class")) {
                        continue;
                    }
                    MetadataReader metadataReader = this.metadataReaderFactory
                            .getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(
                                metadataReader);
                        sbd.setResource(resource);
                        sbd.setSource(resource);
                        if (isCandidateComponent(sbd)) {
                            this.components.add(sbd);
                        } 
                    } 
                } 
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during scanning", ex);
        }
    }

	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException{
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
	}
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }
	public void setIncludeFilters(List<TypeFilter> list) {
		includeFilters = list;
	}
	public void setExcludeFilters(List<TypeFilter> list) {
		excludeFilters = list;
	}
	public void addIncludeFilters(TypeFilter filter){
		includeFilters.add(filter);
	}
	public void addExcludeFilters(TypeFilter filter){
		excludeFilters.add(filter);
	}
    
    
}
