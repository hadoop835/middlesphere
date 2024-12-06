package io.middlesphere.mybatis.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.middlesphere.access.datalimit.DataLimitColumn;
import io.middlesphere.access.datalimit.DataLimitDatabase;
import io.middlesphere.access.datalimit.DataLimitTable;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
public final class SQLParserHandler {
    private final static Map<String, Set<SQLExprTableSource>>  exprTableSources = Maps.newHashMap();
    private DataLimitDatabase dataLimitDatabase;
    private  String   boundSql;

    private  DbType dbType;

    public SQLParserHandler(DataLimitDatabase dataLimitDatabase,String   boundSql,DbType dbType){
           this.dataLimitDatabase = dataLimitDatabase;
           this.boundSql = boundSql;
           this.dbType =dbType;
    }


    public    String   boundSql(){
        if(ObjectUtils.isEmpty(dataLimitDatabase)){
            return this.boundSql;
        }
        SQLExpr sqlExpr =  SQLUtils.toSQLExpr(boundSql,dbType);
        //处理查询语句
        if(sqlExpr instanceof SQLQueryExpr sqlQueryExpr){
            SQLSelectQuery sqlSelectQuery =  sqlQueryExpr.getSubQuery().getQuery();
            if(sqlSelectQuery instanceof  MySqlSelectQueryBlock queryBlock){
                //解析from表
                Set<SQLExprTableSource> sqlExprTableSources =  exprTableSources.get(boundSql);
                if(ObjectUtils.isEmpty(sqlExprTableSources)){
                    sqlExprTableSources = Sets.newHashSet();
                    SQLTableSource sqlTableSource =  queryBlock.getFrom();
                    toSQLTableSource(sqlTableSource,sqlExprTableSources);
                    exprTableSources.put(boundSql,sqlExprTableSources);
                    sqlExprTableSources = exprTableSources.get(boundSql);
                }
                Map<String, DataLimitTable>  dataLimitTables =  dataLimitDatabase.getDataLimitTables().stream().collect(Collectors.toMap(DataLimitTable::getName, Function.identity()));
                SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr)queryBlock.getWhere();
                where(sqlBinaryOpExpr,dataLimitTables,sqlExprTableSources);
            }
        }
        return sqlExpr.toString();
    }

    /**
     * 递归表
     * @param sqlTableSource
     * @param sqlExprTableSources
     */
    private   void   toSQLTableSource(SQLTableSource sqlTableSource, Set<SQLExprTableSource> sqlExprTableSources){
        if(sqlTableSource instanceof SQLExprTableSource sqlExprTableSource){
            sqlExprTableSources.add(sqlExprTableSource);
        } else if (sqlTableSource instanceof SQLJoinTableSource sqlJoinTableSource) {
            toSQLTableSource(sqlJoinTableSource.getLeft(),sqlExprTableSources);
            toSQLTableSource(sqlJoinTableSource.getRight(),sqlExprTableSources);
        }
    }


    private  void   where(SQLBinaryOpExpr sqlBinaryOpExpr,Map<String,DataLimitTable>  dataLimitTables,Set<SQLExprTableSource> sqlExprTableSources){
       if(ObjectUtils.isNotEmpty(dataLimitTables)){
           for(SQLExprTableSource sqlExprTableSource : sqlExprTableSources){
               String alias =  sqlExprTableSource.getAlias();
               DataLimitTable dataLimitTable =  dataLimitTables.get(sqlExprTableSource.getTableName());
               if(ObjectUtils.isNotEmpty(dataLimitTable)){
                   List<DataLimitColumn> dataLimitColumns = dataLimitTable.getDataLimitColumns();
                   StringBuilder  sql = new StringBuilder();
                   for(DataLimitColumn dataLimitColumn : dataLimitColumns){
                       if(ObjectUtils.isNotEmpty(alias)){
                           sql.append(alias).append(".").append(dataLimitColumn.getName()).append(dataLimitColumn.getOp()).append(dataLimitColumn.getValue());
                       }else{
                           sql.append(dataLimitColumn.getName()).append(dataLimitColumn.getOp()).append(dataLimitColumn.getValue());
                       }
                   }
                   //
                   if(ObjectUtils.isNotEmpty(sqlBinaryOpExpr)){
                       sqlBinaryOpExpr.setRight(SQLUtils.toSQLExpr(" and "+sql.toString(),dbType));
                   }else{
                       sqlBinaryOpExpr.setRight(SQLUtils.toSQLExpr(" where "+sql.toString(),dbType));
                   }
               }
           }
       }
    }
}
