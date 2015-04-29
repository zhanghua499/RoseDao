package rose.expl.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rose.expl.ExprResolver;
import rose.expl.ExqlContext;
import rose.expl.ExqlPattern;
import rose.expl.ExqlUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 实现语句的执行接口。
 */
public class ExqlPatternImpl implements ExqlPattern {

    // 输出日志
    private static final Log logger = LogFactory.getLog(ExqlPattern.class);

    // 语句的缓存
    private static final ConcurrentHashMap<String, ExqlPattern> cache = new ConcurrentHashMap<String, ExqlPattern>();

    // 编译的语句
    protected final String pattern;

    // 输出的单元
    protected final ExqlUnit unit;

    /**
     * 构造语句的执行接口。
     * 
     * @param pattern - 编译的语句
     * @param unit - 输出的单元
     */
    protected ExqlPatternImpl(String pattern, ExqlUnit unit) {
        this.pattern = pattern;
        this.unit = unit;
    }

    /**
     * 从语句编译: ExqlPattern 对象。
     * 
     * @param pattern - 待编译的语句
     * 
     * @return ExqlPattern 对象
     */
    public static ExqlPattern compile(String pattern) {

        // 从缓存中获取编译好的语句
        ExqlPattern compiledPattern = cache.get(pattern);
        if (compiledPattern == null) {

            // 输出日志
            if (logger.isDebugEnabled()) {
                logger.debug("EXQL pattern compiling:\n    pattern: " + pattern);
            }

            // 重新编译语句
            ExqlCompiler compiler = new ExqlCompiler(pattern);
            compiledPattern = compiler.compile();

            // 语句的缓存
            cache.putIfAbsent(pattern, compiledPattern);
        }

        return compiledPattern;
    }

    public String execute(ExqlContext context, Map<String, ?> map) throws Exception {

        // 执行转换
        return execute(context, new ExprResolverImpl(map));
    }

    public String execute(ExqlContext context, Map<String, ?> mapVars, Map<String, ?> mapConsts)
            throws Exception {

        // 执行转换
        return execute(context, new ExprResolverImpl(mapVars, mapConsts));
    }

    // 执行转换
    protected String execute(ExqlContext context, ExprResolver exprResolver) throws Exception {

        // 转换语句内容
        unit.fill(context, exprResolver);

        String flushOut = context.flushOut();

        // 输出日志
        if (logger.isDebugEnabled()) {
            logger.debug("EXQL pattern executing:\n    origin: " + pattern + "\n    result: "
                    + flushOut + "\n    params: " + Arrays.toString(context.getParams()));
        }

        return flushOut;
    }

    // 进行简单测试
    public static void main(String... args) throws Exception {
    	main1();
//    	main2();
//    	main3();
    }
    
    
    public static void main1() throws Exception {
//    	String sql1 = "select * from ##(:1.a)";
//    	String sql2 = "select * from #if(:1.c == 0 and :1.d == 0){##(:1.a)} #else {##(:1.b)}";
    	String sql3 = "select * from #for(v in :2){##(:1.a ) }";
    	ExqlContext context = new ExqlContextImpl(1024);
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	TestBean bean = new TestBean("aa","bbbbbb",3,0);
    	byte[] a = new byte[bean.c];
    	map.put(":1", bean);
    	map.put(":2", a);
    	System.out.println(ExqlPatternImpl.compile(sql3).execute(context, map));
    	Object objs  = context.getParams();
    	System.out.println(objs);
    }
    
    public static void main2() throws Exception {
    	String sql = "select * from #if(:1.c == 0){##(:1.a)} #else {##(:1.b)}";
    	ExqlPattern pattern = ExqlPatternImpl.compile(sql);
    	ExqlContext context = new ExqlContextImpl(1024);
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	TestBean bean = new TestBean("aa","bb",1,0);  	
    	map.put(":1", bean);
    	System.out.println(pattern.execute(context, map));
    	Object objs  = context.getParams();
    	System.out.println(objs);   	
    }
    
    public static void main3() throws Exception {

        // 编译下列语句
        ExqlPattern pattern = ExqlPatternImpl
                .compile("SELECT #(:expr1.length()), :expr2.class.name,"
                        + " ##(:expr3) WHERE #if(:expr4) {e = :expr4} #else {e IS NULL}"
                        + "#for(variant in :expr5.bytes) { AND c = :variant}" // NL
                        + " GROUP BY #!(:expr1) ASC");

        ExqlContext context = new ExqlContextImpl(1024);

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("expr1", "expr1");
        map.put("expr2", "expr2");
        map.put("expr3", "expr3");
        map.put("expr4", "expr4");
        map.put("expr5", "expr5");

        System.out.println(pattern.execute(context, map));
        System.out.println(Arrays.toString(context.getParams()));
    }
    
    

    
}