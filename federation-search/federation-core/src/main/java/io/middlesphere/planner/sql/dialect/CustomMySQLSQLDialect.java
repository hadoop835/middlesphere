package io.middlesphere.planner.sql.dialect;

import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;

/**
 * @author Administrator
 */
public final class CustomMySQLSQLDialect extends MysqlSqlDialect {
    public static final SqlDialect DEFAULT = new CustomMySQLSQLDialect(DEFAULT_CONTEXT);
    public CustomMySQLSQLDialect(Context context) {
        super(context);
    }
    @Override
    public void quoteStringLiteral(final StringBuilder builder, final String charsetName, final String value) {
        builder.append(literalQuoteString);
        builder.append(value.replace(literalEndQuoteString, literalEscapedQuote));
        builder.append(literalEndQuoteString);
    }

}
