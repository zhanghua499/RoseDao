package rose.expl.impl;

import rose.expl.ExprResolver;
import rose.expl.ExqlContext;
import rose.expl.ExqlUnit;
import rose.expl.util.ExqlUtils;

/**
 * 直接输出表达式内容的语句单元, 例如: '##(:expr)' 形式的表达式，内容不经转义输出。
 */
public class JoinExprUnit implements ExqlUnit {

    private final String expr;

    /**
     * 构造输出表达式内容的语句单元。
     * 
     * @param text - 输出的表达式
     */
    public JoinExprUnit(String expr) {
        this.expr = expr;
    }


    public boolean isValid(ExprResolver exprResolver) {

        // 解释表达式内容
        Object obj = ExqlUtils.execExpr(exprResolver, expr);

        // 表达式内容有效
        return ExqlUtils.isValid(obj);
    }


    public void fill(ExqlContext exqlContext, ExprResolver exprResolver) throws Exception {

        // 解释表达式内容
        Object obj = exprResolver.executeExpr(expr);

        // 直接输出未经转义的对象内容
        exqlContext.fillText(String.valueOf(obj));
    }
}
