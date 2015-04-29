package rose.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SQLParam {

    /**
     * 指出这个值是 SQL 语句中哪个参数的值
     * 
     * @return 对应 SQL 语句中哪个参数
     */
    String value();
}
