package io.middlesphere.federation.config;

import com.google.common.collect.Lists;

import java.util.List;

public class FederationJdbcConfig {
    private String version="1.0.0";
    private List<FederationJdbcProperty> schemas= Lists.newArrayList();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<FederationJdbcProperty> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<FederationJdbcProperty> schemas) {
        this.schemas = schemas;
    }
}
