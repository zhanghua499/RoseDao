package demo;

import java.util.List;

import rose.dao.DaoFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import demo.dao.UserDao;
import demo.model.User;
import demo.service.UserService;

public class RoseDemo {

	private final static ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"DataSource.xml"});
	private static UserService serivce = null;
	private static UserDao dao;
	
	static{
		serivce = getBean("userService");
		dao  = getDao();
	}
	
	public static void main(String[] args) {

		getUserById(1000);
		getUsersByType();
		getUserBySexAndType();
		getUserByUser();
		getUserByUserRowMapper();
		delUser();
		addUser();
		uptUser();
	}
	
	public static void getUserById(int id){
		User user = serivce.getUserById(id);
		System.out.println(user.toString());
		line();
	}
	
	public static void getUsersByType(){
		List<User> users = dao.getUsersByType("C");
		for(User user:users){
			System.out.println(user.toString());
		}		
		line();
	}
	
	
	public static void getUserBySexAndType(){
		List<User> users = dao.getUserBySexAndType("C","f");
		for(User user:users){
			System.out.println(user.toString());
		}		
		line();
	}
	
	public static void getUserByUser(){
		User u = new User();
		u.setSex("m");
		u.setType("B");
		List<User> users = dao.getUserByUser(u);
		for(User user:users){
			System.out.println(user.toString());
		}		
		line();
	}
	
	public static void getUserByUserRowMapper(){
		User u = new User();
		u.setSex("m");
		u.setType("B");
		List<User> users = dao.getUserByUserRowMapper(u);
		for(User user:users){
			System.out.println(user.toString());
		}		
		line();
	}
	
	public static void delUser(){
		User u = new User();
		u.setNick("zhanghua");
		dao.delUserByNick(u);
		line();
	}
	
	public static void addUser(){
		User u = new User();
		u.setNick("zhanghua"+Math.random());
		u.setLevel(100);
		u.setSex("m");
		u.setType("C");
		dao.addUser(u);
		line();
	}
	
	
	public static void uptUser(){
		User u = new User();
		u.setNick("zhanghua"+Math.random());
		u.setId(1);
		dao.updateUserByNick(u);
		getUserById(1);
		
		getUserById(2);
		u = new User();
		u.setId(2);
		getUserById(2);
	}
	
	
	
	
	
	
	
	private static UserDao getDao(){
		DaoFactory factory = new DaoFactory(ctx);
		return factory.getDao(UserDao.class);
	}	
	
	private static void line(){
		System.out.println("**************************************************************");
		System.out.println();
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T> T  getBean(String beanName){
		return (T) ctx.getBean(beanName);
	}

}
