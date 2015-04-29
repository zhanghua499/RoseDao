package rose.scan;

import java.util.ArrayList;

public abstract class ComponentSearch {

	protected ArrayList<Object> components = new ArrayList<Object>();
	
	//组件包名称
	protected String componentName;

	//组件后缀名称
	protected String componentSuffix;
	
	//测试方法
//	public static void main(String[] args) throws Exception{
//		 //ComponentSearch cs = new ClassPathComponentSearch("dao","Dao");
//		 ComponentSearch cs = new ClassPathComponentSearch("dao","Dao",new ComponentFilter(){
//			 
//			public boolean accept(ComponentInfo compenent) {
//				if(compenent.getComponentClass().getAnnotation(DAO.class) != null)
//					return true;
//				else
//					return false;
//			}
//			 
//		 });
//		 for (Object action: cs.getComponents()) {
//			 System.out.println(((ComponentInfo)action).getComponentClass().getName());
//			 Object instance  = action.getComponentClass().newInstance();
//			 ArrayList<Method> methods = action.getComponents();
//			for (Method method : methods) {
//				method.invoke(instance);
//			}
//		 }
//	}
	
	//扫描类加载路径，取得相应的组件。
	public abstract void searchComponents();
	
	/////////////////////////////////////GET AND SET/////////////////////////////////////////////////////////
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getComponentSuffix() {
		return componentSuffix;
	}
	public void setComponentSuffix(String componentSuffix) {
		this.componentSuffix = componentSuffix;
	}
	public ArrayList<Object> getComponents() {
		return components;
	}
	
}
