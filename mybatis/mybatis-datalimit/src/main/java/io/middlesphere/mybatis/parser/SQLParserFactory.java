package io.middlesphere.mybatis.parser;

import com.alibaba.druid.DbType;
import io.middlesphere.access.datalimit.DataLimitDatabase;

/**
 * @author Administrator
 */
public final class SQLParserFactory {
    private SQLParserFactory(){}


    public  static  SQLParserHandler  handler(DataLimitDatabase dataLimitDatabase, String   boundSql, DbType dbType){
        return new SQLParserHandler(dataLimitDatabase,boundSql,dbType);
    }
}
