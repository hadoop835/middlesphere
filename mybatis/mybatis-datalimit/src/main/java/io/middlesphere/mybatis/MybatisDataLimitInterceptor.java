package io.middlesphere.mybatis;

import com.alibaba.druid.DbType;
import io.middlesphere.access.context.DataLimitContext;
import io.middlesphere.access.datalimit.DataLimitDatabase;
import io.middlesphere.mybatis.parser.SQLParserFactory;
import io.middlesphere.mybatis.parser.SQLParserHandler;
import io.middlesphere.mybatis.util.PluginUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Objects;

/**
 * 数据权限
 * @author Administrator
 */
@Intercepts(
        {@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MybatisDataLimitInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        Objects.requireNonNull(statementHandler);
        //获取元数据类型
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        Objects.requireNonNull(metaObject);
        //获取mapper
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        Objects.requireNonNull(mappedStatement);
        //获取参数
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
        Object parameterObject = parameterHandler.getParameterObject();
        // 获取原始执行的SQL
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        // user  createBy = #{createBy} orgId in (1,2,3)
        DataLimitDatabase dataLimitDatabase = DataLimitContext.getDataLimitContext();
        SQLParserHandler sqlParserHandler = SQLParserFactory.handler(dataLimitDatabase,sql,DbType.mysql);
        metaObject.setValue("delegate.boundSql.sql", sqlParserHandler.boundSql());
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
