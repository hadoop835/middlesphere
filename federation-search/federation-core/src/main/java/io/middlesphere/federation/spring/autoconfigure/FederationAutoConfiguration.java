package io.middlesphere.federation.spring.autoconfigure;
import io.middlesphere.federation.config.FederationConfig;
import io.middlesphere.federation.spring.FederationDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 */
@Configuration
public class FederationAutoConfiguration {

    @Bean
    public FederationDataSource federationDataSource(FederationConfig federationConfig){
        return new FederationDataSource(federationConfig);
    }

}