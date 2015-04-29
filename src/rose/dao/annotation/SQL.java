package rose.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SQL {

    /**
     * 
     * @return 支持的SQL语句
     */
    String value();

    /**
     * 返回该语句的类型，查询类型或变更类型。
     * 默认只有以SELECT开始的才是查询类型，其他的为变更类型。开发者通过这个属性用来变更默认的处理!
     * 
     * @return 查询类型
     */
    SQLType type() default SQLType.UN_KNOW;
}
