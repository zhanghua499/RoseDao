package rose.dao.interpreter;

public class DefaultSQLInterpreterResult implements SQLInterpreterResult {

	private String sql;
	private Object[] parameters;
	
	public DefaultSQLInterpreterResult(){
		
	}
	
	public DefaultSQLInterpreterResult(String sql,Object[] parameters){
		this.sql = sql;
		this.parameters = parameters;
	}
	
	public String getSQL() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	

}
