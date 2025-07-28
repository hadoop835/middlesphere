package io.middlesphere.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 动态解析sql
 */
public class SQLUtil {

    /**
     *
     * @param sql
     * @param criteria
     * @return
     */
    public static String  toSQL(String sql,Map<String,Object> criteria){
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if(statement instanceof Select select) {
                if (select instanceof SetOperationList setOperationList) {
                    List<Select> selectList = setOperationList.getSelects();
                    if(CollectionUtil.isNotEmpty(selectList)){
                        for (Select unionSelect : selectList) {
                            PlainSelect unionPlainSelect =   unionSelect.getPlainSelect();
                            toJoin(unionPlainSelect.getJoins(),criteria);
                            Expression where =  unionPlainSelect.getWhere();
                            if(ObjectUtil.isNotEmpty(where)){
                                Expression newWhere = dynamicExpression(where,criteria);
                                unionPlainSelect.setWhere(newWhere);
                            }
                        }
                    }
                } else if (select instanceof PlainSelect) {
                    PlainSelect plainSelect =   select.getPlainSelect();
                    List<Join> joins =  plainSelect.getJoins();
                    toJoin(joins,criteria);
                    Expression expression =  dynamicExpression(plainSelect.getWhere(),criteria);
                    plainSelect.setWhere(expression);
                    System.out.println(plainSelect.toString());
                }
            }

            return statement.toString();
        } catch (JSQLParserException e) {
            throw new RuntimeException("解析sql语句异常",e);
        }
    }


    /**
     * 动态生成where条件
     * @param expression
     * @param criteria
     * @return
     */
    private static Expression dynamicExpression(Expression expression, Map<String,Object> criteria) {
        if (ObjectUtil.isEmpty(expression)) {
            return null;
        }
        //处理AND
        if (expression instanceof AndExpression andExpression) {
            Expression leftExpression = dynamicExpression(andExpression.getLeftExpression(), criteria);
            Expression rightExpression = dynamicExpression(andExpression.getRightExpression(), criteria);
            if(ObjectUtil.isEmpty(leftExpression) && ObjectUtil.isEmpty(rightExpression)){
                return null;
            } else if (ObjectUtil.isEmpty(leftExpression)) {
                return rightExpression;
            } else if (ObjectUtil.isEmpty(rightExpression)) {
                return leftExpression;
            }else{
                if(rightExpression instanceof OrExpression){
                    return new OrExpression(leftExpression,rightExpression);
                }else{
                    return new AndExpression(leftExpression,rightExpression);
                }
            }
        }
        //处理OR
        else if(expression instanceof OrExpression orExpression){
            Expression leftExpression = dynamicExpression(orExpression.getLeftExpression(), criteria);
            Expression rightExpression = dynamicExpression(orExpression.getRightExpression(), criteria);
            if(ObjectUtil.isEmpty(leftExpression) && ObjectUtil.isEmpty(rightExpression)){
                return null;
            } else if (ObjectUtil.isEmpty(leftExpression)) {
                return rightExpression;
            } else if (ObjectUtil.isEmpty(rightExpression)) {
                return leftExpression;
            }else{
                if(rightExpression instanceof AndExpression ){
                    return new AndExpression(leftExpression,rightExpression);
                }else{
                    return new OrExpression(leftExpression,rightExpression);
                }

            }
        }
        //处理比较符
        else if (expression instanceof ComparisonOperator comparisonOperator) {
            Expression rightExpression =  comparisonOperator.getRightExpression();
            if(placeholder(rightExpression.toString())){
                String columnValue = parserSpelKey(criteria,getVariable(rightExpression.toString()));
                if(ObjectUtil.isNotEmpty(columnValue)){
                    comparisonOperator.setRightExpression(new StringValue(columnValue));
                }else{
                    return null;
                }
            }
        }
        // 处理 LIKE 表达式（例如：col LIKE '%val%'）
        else if (expression instanceof LikeExpression likeExpression) {
            Expression rightExpression = likeExpression.getRightExpression();
            if(placeholder(rightExpression.toString())){
                String columnValue = parserSpelKey(criteria,getVariable(rightExpression.toString()));
                if(ObjectUtil.isNotEmpty(columnValue)){
                    String likeValue = getLikeValue(rightExpression.toString(),columnValue);
                    likeExpression.setRightExpression(new StringValue(likeValue));
                }else{
                    return null;
                }
            }
        }
        // 处理 IN 表达式（例如：col IN (1,2,3)）
        else if (expression instanceof InExpression inExpression){
            Expression left =  dynamicExpression(inExpression.getLeftExpression(),criteria);
            Expression right =  dynamicExpression(inExpression.getRightExpression(),criteria);
            if(right.toString().startsWith("(")){
                inExpression.setLeftExpression(left);
                inExpression.setRightExpression(right);
                return inExpression;
            }
            return right;
        }

        //处理括号
        else if (expression instanceof ParenthesedExpressionList parenthesedExpression){
            //
            if(parenthesedExpression.get(0) instanceof StringValue value){
                if (placeholder(value.toString())) {
                    String columnValue = parserSpelKey(criteria, getVariable(value.toString()));
                    if (ObjectUtil.isNotEmpty(columnValue)) {
                        parenthesedExpression.set(0,columnValue);
                    }else{
                        return null;
                    }
                }
            }
            // and
            else if (parenthesedExpression.get(0) instanceof AndExpression andExpression) {
                return dynamicExpression(andExpression,criteria);
            }
            // or
            else if (parenthesedExpression.get(0) instanceof OrExpression orExpression) {
                return dynamicExpression(orExpression,criteria);
            }
        }

        //Between
        else if (expression instanceof  Between between) {
            Expression expressionStart =  between.getBetweenExpressionStart();
            if(expressionStart instanceof  StringValue value){
                if(placeholder(value.toString())){
                    String columnValue = parserSpelKey(criteria, getVariable(value.toString()));
                    if (ObjectUtil.isNotEmpty(columnValue)) {
                        between.setBetweenExpressionStart(new StringValue(columnValue));
                    }else {
                        return null;
                    }
                }
            }
            Expression expressionEnd = between.getBetweenExpressionEnd();
            if(expressionEnd instanceof  StringValue value){
                if(placeholder(value.toString())){
                    String columnValue = parserSpelKey(criteria, getVariable(value.toString()));
                    if (ObjectUtil.isNotEmpty(columnValue)) {
                        between.setBetweenExpressionStart(new StringValue(columnValue));
                    }else {
                        return null;
                    }
                }
            }
        }
        //处理子查询
        if(expression instanceof  ParenthesedSelect parenthesedSelect){
            Select fromSelect = parenthesedSelect.getSelect();
            if(fromSelect instanceof SetOperationList setOperationList){
                List<Select> selectList = setOperationList.getSelects();
                if(CollectionUtil.isNotEmpty(selectList)){
                    for (Select unionSelect : selectList) {
                        PlainSelect unionPlainSelect =  unionSelect.getPlainSelect();
                        toJoin(unionPlainSelect.getJoins(),criteria);
                        Expression where =  unionPlainSelect.getWhere();
                        if(ObjectUtil.isNotEmpty(where)){
                            Expression newWhere = dynamicExpression(where,criteria);
                            unionPlainSelect.setWhere(newWhere);
                        }
                    }
                }
            }else if (fromSelect instanceof PlainSelect plainSelect){
                List<Join>  joins = plainSelect.getJoins();
                if(CollectionUtil.isNotEmpty(joins)){
                    for(Join join : joins){
                        List<Expression> newExpressions = Lists.newArrayList();
                        Collection<Expression> expressions =  join.getOnExpressions();
                        if(CollectionUtil.isNotEmpty(expressions)){
                            for(Expression joinExpression : expressions){
                                Expression newSubWhere =   dynamicExpression(joinExpression,criteria);
                                if(ObjectUtil.isNotEmpty(newSubWhere)){
                                    newExpressions.add(newSubWhere);
                                }
                            }
                        }
                        join.setOnExpressions(newExpressions);
                    }
                }
                Expression subWhere = plainSelect.getWhere();
                if(ObjectUtil.isNotEmpty(subWhere)){
                    Expression newSubWhere = dynamicExpression(subWhere,criteria);
                    plainSelect.setWhere(newSubWhere);
                }

            }
        }

        return expression;
    }


    /**
     * 处理join
     * @param joins
     * @param criteria
     */
    private  static   void  toJoin(List<Join> joins,Map<String,Object> criteria){
        if(CollectionUtil.isNotEmpty(joins)){
            for(Join join : joins){
                List<Expression> expressionList = Lists.newArrayList();
                Collection<Expression> expressions =  join.getOnExpressions();
                for(Expression expression : expressions){
                    Expression newExpression =   dynamicExpression(expression,criteria);
                    if(ObjectUtil.isNotEmpty(newExpression)){
                        expressionList.add(newExpression);
                    }
                }
                join.setOnExpressions(expressionList);
            }
        }
    }



    /**
     * 获取值
     * @param criteria
     * @param key
     * @return
     */
    private static String  parserSpelKey(Map<String,Object> criteria, String key) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        ExpressionParser parser = new SpelExpressionParser();
        if(CollectionUtil.isNotEmpty(criteria)){
            for(String columnAlias : criteria.keySet()){
                Object value =  criteria.get(key);
                context.setVariable(columnAlias.substring(columnAlias.indexOf("{")+1,columnAlias.indexOf("}")), value);
            }
        }
        return (String)parser.parseExpression(key).getValue(context);
    }



    /**
     * 判断占位符号
     * @param expression
     * @return
     */
    private static    boolean  placeholder(String  expression){
        if(expression.contains("#{") || expression.contains("${")){
            return true;
        }
        return false;
    }

    /**
     * 获取变量
     * @param expression
     * @return
     */
    private  static   String getVariable(String expression){
        return "#"+expression.substring(expression.indexOf("{")+1,expression.indexOf("}"));
    }


    /**
     * 处理like表达式
     * @param expression
     * @param value
     * @return
     */
    private   static  String  getLikeValue(String expression,String value){
        if(expression.contains("#{")){
            return expression.substring(0,expression.indexOf("#{"))+value+expression.substring(expression.indexOf("}")+1,expression.length());
        }else if(expression.contains("${")){
            return expression.substring(0,expression.indexOf("${"))+value+expression.substring(expression.indexOf("}")+1,expression.length());
        }
        return expression;
    }
}
