package io.middlesphere.access.service.impl;

import com.google.common.collect.Sets;
import io.middlesphere.access.api.IDataLimitProvider;
import io.middlesphere.access.datalimit.DataLimitColumn;
import io.middlesphere.access.datalimit.DataLimitDatabase;
import io.middlesphere.access.datalimit.DataLimitTable;
import io.middlesphere.access.service.IDataLimitService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Administrator
 */
public class DefaultDataLimitServiceImpl implements IDataLimitService {

    private IDataLimitProvider dataLimitProvider;

    public DefaultDataLimitServiceImpl(IDataLimitProvider dataLimitProvider){
            this.dataLimitProvider = dataLimitProvider;
    }
    @Override
    public DataLimitDatabase queryByUserId(String userId) {
        Map<String,Object> loginInfo =  this.dataLimitProvider.getLoginInfoByUserId(userId);
        EvaluationContext context = new StandardEvaluationContext();
        if(CollectionUtils.isEmpty(loginInfo)){
            for(String key : loginInfo.keySet()){
                context.setVariable(key, loginInfo);
            }
        }
        DataLimitDatabase dataLimitDatabase = this.dataLimitProvider.queryByUserId(userId);
        Set<DataLimitTable> dataLimitTables =  dataLimitDatabase.getDataLimitTables();
        Set<DataLimitColumn> dataLimitColumnSet = Sets.newHashSet();
        for(DataLimitTable dataLimitTable : dataLimitTables){
            List<DataLimitColumn> dataLimitColumns = dataLimitTable.getDataLimitColumns();
            if(!CollectionUtils.isEmpty(dataLimitColumns)){
                 for(DataLimitColumn dataLimitColumn : dataLimitColumns){
                     if(dataLimitColumn.getValue().toString().startsWith("#")){
                         if(dataLimitColumn.getOp().toUpperCase().contains("IN")){

                         }else{

                         }
                     }
                 }
            }
        }



       // create_by = #{createBy}

        return null;
    }

    /**
     *
     * @param expressionStr
     * @param context
     */
    private   void  parseExpression(String expressionStr,EvaluationContext context,DataLimitColumn dataLimitColumn){
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression =  parser.parseExpression(expressionStr);
        Object object = expression.getValue(context);
        if(object instanceof List<?> value){

        }else if(object instanceof Set<?> value){

        }else if(object instanceof Integer value){
            dataLimitColumn.setValue(value);
        }else if(object instanceof String value){
            dataLimitColumn.setValue(value);
        }else if(object instanceof Long value){
            dataLimitColumn.setValue(value);
        }
    }
}
