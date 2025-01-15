package io.middlesphere.federation.config;
import com.zaxxer.hikari.HikariConfig;
import io.middlesphere.federation.spring.autoconfigure.FederationMybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Administrator
 */
@ConfigurationProperties(prefix = "federation.config")
public class FederationConfig {
    private boolean  caseSensitive =false;

    private  FederationJdbcConfig jdbcConfig;

    private FederationMybatisProperties mybatis;

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public FederationJdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(FederationJdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    public FederationMybatisProperties getMybatis() {
        return mybatis;
    }

    public void setMybatis(FederationMybatisProperties mybatis) {
        this.mybatis = mybatis;
    }
}
