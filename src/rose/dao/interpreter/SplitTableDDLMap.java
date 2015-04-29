package rose.dao.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

public class SplitTableDDLMap implements ApplicationContextAware{
	
	protected ApplicationContext applicationContext;
	protected JdbcTemplate jt;
	
	//分表DDL语句保存Map
	private Map<String,String> ddlMap = new HashMap<String,String>();
	
	public SplitTableDDLMap(Map<String,String> map){
		this.ddlMap = map;
	}

	public Map<String,String> getDdlMap() {
		return ddlMap;
	}

	public void setDdlMap(Map<String,String> ddlMap) {
		this.ddlMap = ddlMap;
	}
	
	public void createTable(String tableName,String tableNameHash){
		Object ddl = ddlMap.get(tableName);
		if(ddl!=null) {
			jt.execute(ddl.toString().replaceAll("#tablename#", tableNameHash));
		}
	}
	

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.applicationContext = ctx;
		this.jt = (JdbcTemplate)ctx.getBean("jdbcTemplate");
		
	}
	
	
}
