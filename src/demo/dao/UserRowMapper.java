package demo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import demo.model.User;

public class UserRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int i) throws SQLException {
		User u = new User();
		int id = rs.getInt("id");
		String nick = rs.getString("nick");
		String sex = rs.getString("sex");
		String type = rs.getString("type");
		
		if("C".equals(type)) type = "客户";
		else if("B".equals(type)) type = "商家";
		
		if("m".equals(sex)) sex = "男";
		else if("f".equals(sex)) sex = "女";
		
		u.setId(id);
		u.setNick(nick);
		u.setSex(sex);
		u.setType(type);
		
		return u;
	}

}
