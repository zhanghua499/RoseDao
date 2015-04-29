package rose.expl.impl;

import rose.expl.ExprResolver;
import rose.expl.ExqlContext;
import rose.expl.ExqlUnit;

/**
 * 输出空白的语句单元, 代替空的表达式。
 * 
 */
public class EmptyUnit implements ExqlUnit {


    public boolean isValid(ExprResolver exprResolver) {
        // Empty unit is always valid.
        return true;
    }

    public void fill(ExqlContext exqlContext, ExprResolver exprResolver) throws Exception {
        // Do nothing.
    }
}
