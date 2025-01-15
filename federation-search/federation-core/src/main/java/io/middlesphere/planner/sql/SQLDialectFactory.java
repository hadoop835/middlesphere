package io.middlesphere.planner.sql;

import com.google.common.collect.Maps;
import io.middlesphere.planner.sql.dialect.CustomMySQLSQLDialect;
import io.middlesphere.planner.sql.dialect.CustomPostgreSQLSQLDialect;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.dialect.MssqlSqlDialect;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.dialect.OracleSqlDialect;

import java.util.Map;

/**
 * @author Administrator
 */
public final class SQLDialectFactory {
    private static final Map<String, SqlDialect> SQL_DIALECTS_REGISTRY = Maps.newHashMap();

    static {
        SQL_DIALECTS_REGISTRY.put("H2", CustomMySQLSQLDialect.DEFAULT);
        SQL_DIALECTS_REGISTRY.put("MySQL", CustomMySQLSQLDialect.DEFAULT);
        SQL_DIALECTS_REGISTRY.put("MariaDB", CustomMySQLSQLDialect.DEFAULT);
        SQL_DIALECTS_REGISTRY.put("Oracle", OracleSqlDialect.DEFAULT);
        SQL_DIALECTS_REGISTRY.put("SQLServer", MssqlSqlDialect.DEFAULT);
        SQL_DIALECTS_REGISTRY.put("PostgreSQL", CustomPostgreSQLSQLDialect.DEFAULT);
        SQL_DIALECTS_REGISTRY.put("openGauss", CustomPostgreSQLSQLDialect.DEFAULT);
    }

    /**
     * Get SQL dialect.
     *
     * @param databaseType database type
     * @return SQL dialect
     */
    public static SqlDialect getSQLDialect(final String databaseType) {
        return SQL_DIALECTS_REGISTRY.getOrDefault(databaseType, MysqlSqlDialect.DEFAULT);
    }
}
