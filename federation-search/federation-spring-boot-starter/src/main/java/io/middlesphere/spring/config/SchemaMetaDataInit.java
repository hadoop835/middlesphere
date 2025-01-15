package io.middlesphere.spring.config;

import io.middlesphere.planner.metadata.SchemaMetaData;
import io.middlesphere.planner.util.SQLPlannerUtils;
import org.apache.calcite.schema.SchemaPlus;
import org.springframework.boot.CommandLineRunner;

import javax.sql.DataSource;

/**
 * @author Administrator
 */
public class SchemaMetaDataInit implements CommandLineRunner {
    private DataSource  dataSource;

    public  SchemaMetaDataInit(DataSource  dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public void run(String... args) throws Exception {
        SchemaMetaData schemaMetaData = new SchemaMetaData(this.dataSource);
        SchemaPlus schemaPlus =  schemaMetaData.createRootSchema();
        SQLPlannerUtils.init(schemaPlus);
    }
}
