package io.middlesphere.parser;

public class SQLUtil {


    /**
     * 动态生成where条件
     * @param expression
     * @param criteriaRequests
     * @return
     */
    private static Expression dynamicExpression(Expression expression, List<AddStatisticalQueryCriteriaRequest> criteriaRequests) {
        if (ObjectUtil.isEmpty(expression)) {
            return null;
        }
    }
}
