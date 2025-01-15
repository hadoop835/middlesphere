package io.middlesphere.planner.sql.dialect;

import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.dialect.PostgresqlSqlDialect;

/**
 * @author Administrator
 */
public final  class CustomPostgreSQLSQLDialect extends PostgresqlSqlDialect {
    public static final SqlDialect DEFAULT = new CustomPostgreSQLSQLDialect(DEFAULT_CONTEXT);
    public CustomPostgreSQLSQLDialect(Context context) {
        super(context);
    }
    @Override
    public void quoteStringLiteral(final StringBuilder builder, final String charsetName, final String value) {
        builder.append(literalQuoteString);
        builder.append(value.replace(literalEndQuoteString, literalEscapedQuote));
        builder.append(literalEndQuoteString);
    }
}
