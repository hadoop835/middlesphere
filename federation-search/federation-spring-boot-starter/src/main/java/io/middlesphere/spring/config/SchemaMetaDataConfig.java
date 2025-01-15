package io.middlesphere.spring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Administrator
 */
@Configuration
public class SchemaMetaDataConfig {

    @Bean
    public  SchemaMetaDataInit schemaMetaDataInit(DataSource dataSource){
        return new SchemaMetaDataInit(dataSource);
    }

}
