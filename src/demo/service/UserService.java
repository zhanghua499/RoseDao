package demo.service;

import org.springframework.beans.factory.annotation.Autowired;

import demo.dao.UserDao;
import demo.model.User;

public class UserService {
	@Autowired
	UserDao dao;
	
	public User getUserById(int id){
		return dao.getUserById(id);
	}
	
	
}
