package rose.dao.interpreter;

import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import java.util.concurrent.ConcurrentHashMap;

import rose.dao.annotation.SplitTable;
import rose.dao.provider.DaoDescription;
@Order(2)
public class SplitTableInterpreter implements SQLInterpreter, ApplicationContextAware {

	protected ApplicationContext applicationContext;
	private static Pattern insert_pattern =  Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);
	private static ConcurrentHashMap<String, Integer> tableMap = new ConcurrentHashMap<String, Integer>();
	@Override
	public SQLInterpreterResult interpret(DataSource dataSource, String sql,
			DaoDescription desc, Map<String, Object> parametersAsMap,
			Object[] parametersAsArray) {
		SplitTable splitTable = desc.getAnnotation(SplitTable.class); 
        if(splitTable != null){
        	String tableName = splitTable.tableName();
        	String[] tableNames = {tableName};
        	if("".equals(tableName))
        		tableNames = splitTable.mulitTableName();
        	String key = splitTable.key();
        	Class<? extends SplitTableRule> rule = splitTable.rule();
        	int pos = splitTable.keypos();
        	if(pos<0)
        		pos = getKeyPosition(sql,key);
        	if(pos>=0){
        		//片键值,如果采取分表的话，则必须要有片键，且目前只支持单片键
        		String value = parametersAsArray[pos].toString();
        		String  hashCode="";
        		if(rule == null|| rule == SplitTable.DefaultRule.class)
        			hashCode = hash(value.toString());
        		else{
        			try {
						SplitTableRule ruleSplit =  rule.newInstance();
						hashCode = ruleSplit.getSplitKey(value.toString());
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
        		}
	        	//String hash = hashCode.substring(hashCode.length()-2, hashCode.length());
        		for(String name:tableNames){
		        	String tableNameHash = name+hashCode;
		        	if(tableMap.get(tableNameHash) == null){
			        	SplitTableDDLMap ddl = (SplitTableDDLMap) applicationContext.getBean("splitTableDDL");
			        	ddl.createTable(name, tableNameHash);
			        	tableMap.put(tableNameHash, 1);
		        	}
		        	sql = sql.replaceAll(name, tableNameHash);
        		}
	        	return new DefaultSQLInterpreterResult(sql,parametersAsArray);
        	}
        }
		return null;
	}
	
	//计算片键所在的参数位置
	private int  getKeyPosition(String sql,String key){
		// 用正则表达式匹配  Insert 语句
		if(insert_pattern.matcher(sql).find()){
			return getInsertKeyPosition(sql,key);
		}
		int index = sql.indexOf(key);
		if(index<0)
			return index;
		sql = sql.substring(0, index);
		int postion = 0;
		char[] cs = sql.toCharArray();
		for(char c:cs){
			if(c=='?'){
				postion++;
			}
		}
		return postion;
	}

	//计算Insert语句片键所在的参数位置
	//片键在插入语句的列中必须存在
	private int getInsertKeyPosition(String sql,String key){
		int beging = sql.indexOf("(")+1;
		int end = sql.indexOf(")");
		String[] fields= sql.substring(beging, end).split(",");
		int postion = -1;
		for(int i =0;i<fields.length;i++){
			if(key.equals(fields[i])){
				postion = i;
				break;
			}
		}
		return postion;
	}
	
	//取得分表的hash值
    private String hash(String s) {
		String  hashCode = s.hashCode()+"";
		return hashCode.substring(hashCode.length()-2, hashCode.length());
    }
    
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.applicationContext = ctx;		
	}
}
