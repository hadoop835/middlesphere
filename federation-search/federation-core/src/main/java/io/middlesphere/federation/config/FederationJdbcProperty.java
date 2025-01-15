package io.middlesphere.federation.config;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public final class FederationJdbcProperty {
    private  String  factory;
    private  String  name;
    private  String  type="custom";
    private  JdbcProperty operand;

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JdbcProperty getOperand() {
        return operand;
    }

    public void setOperand(JdbcProperty operand) {
        this.operand = operand;
    }

    public  static  class  JdbcProperty{
              private  String  dataSource;
              private  String  jdbcUrl;
              private  String  jdbcDriver;
              private  String   jdbcUser;
              private  String   jdbcPassword;
              private  String   jdbcCatalog;
              private String   jdbcSchema;
              private  String  sqlDialectFactory;

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getJdbcDriver() {
            return jdbcDriver;
        }

        public void setJdbcDriver(String jdbcDriver) {
            this.jdbcDriver = jdbcDriver;
        }

        public String getJdbcUser() {
            return jdbcUser;
        }

        public void setJdbcUser(String jdbcUser) {
            this.jdbcUser = jdbcUser;
        }

        public String getJdbcPassword() {
            return jdbcPassword;
        }

        public void setJdbcPassword(String jdbcPassword) {
            this.jdbcPassword = jdbcPassword;
        }

        public String getJdbcCatalog() {
            return jdbcCatalog;
        }

        public void setJdbcCatalog(String jdbcCatalog) {
            this.jdbcCatalog = jdbcCatalog;
        }

        public String getJdbcSchema() {
            return jdbcSchema;
        }

        public void setJdbcSchema(String jdbcSchema) {
            this.jdbcSchema = jdbcSchema;
        }

        public String getSqlDialectFactory() {
            return sqlDialectFactory;
        }

        public void setSqlDialectFactory(String sqlDialectFactory) {
            this.sqlDialectFactory = sqlDialectFactory;
        }
    }

}
