package rose.scan;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class ComponentInfo {
	private ArrayList<Method> components;
	private Class<?> componentClass;
	private ArrayList<String> componentPath;
	
	public ComponentInfo(String componentClassPath) throws ClassNotFoundException{
		this.componentPath = new ArrayList<String>();
		this.components = new ArrayList<Method>();
		init(componentClassPath);
		
	}
	
	public Class<?> getComponentClass(){
		return this.componentClass;
	}
	
	public ArrayList<String> getComponentPath(){
		return this.componentPath;
	}
	
	public ArrayList<Method> getComponents(){
		return this.components;
	}
	
	private void init(String componentClassPath) throws ClassNotFoundException{
		String path = StringUtils.removeEnd(componentClassPath, ".class");
		String className = path.replaceAll("/", ".");
		this.componentClass = Class.forName(className);
		Method[] methods  = this.componentClass.getDeclaredMethods();
		for (Method method : methods) {
			components.add(method);
			componentPath.add(path+method.getName());
		}
	}
}
