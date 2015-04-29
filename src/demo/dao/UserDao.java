package demo.dao;

import java.util.List;

import demo.model.User;
import rose.dao.annotation.DAO;
import rose.dao.annotation.RowHandler;
import rose.dao.annotation.SQL;

@DAO
public interface UserDao {
	
	@SQL("select * from mst_user where id = :1 ")
	public User getUserById(int id);
	
	@SQL("select * from mst_user where type = :1 limit 10 ")
	public List<User> getUsersByType(String type);
	
	@RowHandler (rowMapper = UserRowMapper.class)
	@SQL("select id,nick,type,sex from mst_user where type = :1.type  and sex = :1.sex limit 10 ")
	public List<User> getUserByUserRowMapper(User user);
	
	@SQL("select nick,type,sex from mst_user where type = :1  and sex = :2 limit 10 ")
	public List<User> getUserBySexAndType(String type,String sex);
	
	@SQL("select nick,type,sex from mst_user where type = :1.type  and sex = :1.sex limit 10 ")
	public List<User> getUserByUser(User user);
	
	@SQL("insert into mst_user (nick,sex,`level`,type,updateTime) " +
			"values (:1.nick,:1.sex,:1.level,:1.type,now())")
	public void addUser(User user);
	
	@SQL("delete from mst_user where nick = :1.nick ")
	public void delUserByNick(User user);
	
	@SQL("update mst_user set #if(:1.nick != null){nick = :1.nick}#else{nick=nick} " +
			" where id = :1.id ")
	public void updateUserByNick(User user);
}
