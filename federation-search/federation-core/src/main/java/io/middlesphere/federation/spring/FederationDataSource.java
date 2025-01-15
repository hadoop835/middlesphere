package io.middlesphere.federation.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.middlesphere.federation.config.FederationConfig;
import io.middlesphere.federation.util.FastjsonObjectMapper;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Administrator
 */
public class FederationDataSource  implements DataSource {
    private  FederationConfig federationConfig;
    public   FederationDataSource(FederationConfig federationConfig){
            this.federationConfig = federationConfig;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("caseSensitive", String.valueOf(federationConfig.isCaseSensitive()));
        properties.put("model","inline:"+ FastjsonObjectMapper.toJSONString(federationConfig.getJdbcConfig()));
        return DriverManager.getConnection("jdbc:calcite:",properties);
    }
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
