package rose.scan;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import rose.scan.classpath.FileObject;
import rose.scan.classpath.FileSystemManager;

public class ClassPathComponentSearch extends ComponentSearch {

	private ComponentFilter componentfilter;
	
	public ClassPathComponentSearch(){
		//默认扫描控制器
		this("action","Action");
		
	}
	public ClassPathComponentSearch(String componentName,String componentSuffix){
		this(componentName,componentSuffix,null);
	}
	public ClassPathComponentSearch(String componentName,String componentSuffix,ComponentFilter componentfilter){
		this.componentName = componentName;
		this.componentSuffix = componentSuffix;
		this.componentfilter = componentfilter;
		searchComponents();
	}
	//扫描类加载路径，取得相应的组件。
	public void searchComponents(){
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		try{
			//取得类加载器集合	
			Enumeration<URL> founds = resourcePatternResolver.getClassLoader().getResources("");
			FileSystemManager fileManager = new FileSystemManager();
			while (founds.hasMoreElements()) {
				 URL urlObject = founds.nextElement();
				 if (!"file".equals(urlObject.getProtocol())) {
	                continue;
	            }
				String path = urlObject.getPath();
	            if (!path.endsWith("/classes/") && !path.endsWith("/bin/")) {
	                continue;
	            }
	            
	            FileObject fo =  fileManager.resolveFile(urlObject);
	            FileObject root = fo;
	            deepWalker(fo,root);
	                
	            //Resource resource = new FileSystemResource(file);
	            //System.out.println(resource.getFilename());
			 }
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//递归便利Class目录，扫描所有目录
	private void deepWalker(FileObject fo,FileObject root) throws IOException, ClassNotFoundException{
		//仅扫描Component包及其子包下的类，加快速度
		if(componentName.equals(fo.getName())){
			//递归扫描Component包及其子包
			checkActionResource(fo,root);
		}
		else{
			//扫描同级目录的其他包及其子包
			FileObject[] children = fo.getChildren();
	        for (FileObject child : children) {	
	            if (child.getType().hasChildren()) {
	            	deepWalker(child,root);
	            }
	        }
		}
	}
	//递归扫描Component包及其子包
	private void checkActionResource(FileObject fo,FileObject root) throws IOException, ClassNotFoundException{
    	FileObject[] children = fo.getChildren();
    	for (FileObject child : children) {
    		String path = root.getRelativeName(child.getURL().getPath());
    		//检查是否为约定的名称
        	if(!skip(path)){
        		//System.out.println(path);
        		//解析并保存Component信息
        		ComponentInfo action = new ComponentInfo(path);
        		//加入上下文
        		if(componentfilter==null||componentfilter.accept(action))
        			components.add(action);
        	}
        	//继续扫描Component的子包
    		if (child.getType().hasChildren()) {
    			checkActionResource(child,root);
           }       		 
    	}
	}
	
	private boolean skip(String classPath){
		//必须为Class文件
		if(!classPath.endsWith("class"))
			return true;
		//不为内部类
		if(classPath.indexOf("$")>0)
			return true;
		//必须以Component结尾
		if(!classPath.replaceFirst(".class", "").endsWith(componentSuffix))
			return true;
		else
			return false;
	}
}
