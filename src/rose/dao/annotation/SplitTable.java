package rose.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rose.dao.interpreter.SplitTableRule;

@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SplitTable {
	
	/*
	 * 指定分表的表名
	 */
	
	String tableName() default "";
	
	/*
	 * 指定多张分表的表名
	 */
	String[] mulitTableName() default {};
	/*
	 * 指定分表的键值
	 * 如果采取分表的话，则必须要有片键，且目前只支持单片键
	 */
	String key() default "";
	
	/*
	 * 指定分表的键值在参数中的位置(与key冲突,优先使用keypos)
	 */
	int keypos() default -1;
	
	/*
	 * 指定分表规则
	 */
	
	Class<? extends SplitTableRule> rule() default DefaultRule.class;
	
	//默认分表规则
	class DefaultRule implements SplitTableRule{

		@Override
		public String getSplitKey(String value) {			
			String  hashCode = value.hashCode()+"";
			return hashCode.substring(hashCode.length()-2, hashCode.length());
		}
		
	}
}
