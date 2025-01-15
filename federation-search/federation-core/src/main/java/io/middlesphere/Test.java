package io.middlesphere;

import org.apache.calcite.config.Lex;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.rel2sql.SqlImplementor;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.util.Properties;

public class Test {
    public static void main(String[] args) throws Exception {
        Properties info = new Properties();
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        Schema schema = new AbstractSchema() {};
        rootSchema.add("MY_SCHEMA", schema);
        Table yourTable = new AbstractTable() {
            @Override
            public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                // 如果要动态分析表，那么就自己去创建
                return typeFactory.builder()

//                        .add("id", typeFactory.createJavaType(int.class))
//                        .add("name", typeFactory.createJavaType(String.class))
//                        .add("age", typeFactory.createJavaType(int.class))
                        .build();
            }
        };
        Table department_table = new AbstractTable() {
            @Override
            public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                // 如果要动态分析表，那么就自己去创建
                return typeFactory.builder()
//                        .add("id", typeFactory.createJavaType(int.class))
//                        .add("department", typeFactory.createJavaType(String.class))
//                        .add("location", typeFactory.createJavaType(String.class))
                        .build();
            }
        };
      rootSchema.getSubSchema("MY_SCHEMA").add("your_table", yourTable);
      rootSchema.getSubSchema("MY_SCHEMA").add("department_table", department_table);
        SqlParser.Config parserConfig = SqlParser.config()
                .withLex(Lex.MYSQL)
                .withConformance(SqlConformanceEnum.MYSQL_5);
        Frameworks.createRootSchema(false);
        FrameworkConfig config = Frameworks.newConfigBuilder()

                .parserConfig(parserConfig)
               // .defaultSchema(rootSchema.getSubSchema("MY_SCHEMA")) // 使用自定义Schema
                .defaultSchema(rootSchema.getSubSchema("MY_SCHEMA"))
                .build();
        Planner planner = Frameworks.getPlanner(config);
        String sql = "SELECT A.id, A.name FROM (SELECT id,name FROM your_table WHERE age > 30 ) A JOIN (SELECT id, department FROM department_table WHERE location = 'NY' ) B ON A.id = B.id WHERE A.id > 100 ";
//        String sql = "SELECT * FROM your_table where id = 1 and name = 'you_name'";
        SqlNode sqlNode = planner.parse(sql);
        SqlNode validatedSqlNode = planner.validate(sqlNode);
        RelRoot relRoot = planner.rel(validatedSqlNode);
        RelNode rootRelNode = relRoot.rel;
        System.out.println(rootRelNode.explain());

        HepProgram hepProgram = new HepProgramBuilder()
                .addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE)
                .addRuleInstance(CoreRules.FILTER_INTO_JOIN)
                .addRuleInstance(CoreRules.FILTER_AGGREGATE_TRANSPOSE)
                .addRuleInstance(CoreRules.FILTER_SET_OP_TRANSPOSE)
                .addRuleInstance(CoreRules.PROJECT_FILTER_TRANSPOSE)
                .addRuleInstance(CoreRules.PROJECT_JOIN_TRANSPOSE)
                .build();
        // 创建HepPlanner
        HepPlanner hepPlanner = new HepPlanner(hepProgram);
        // 设置根RelNode
        hepPlanner.setRoot(rootRelNode);
        // 进行优化
        RelNode optimizedRelNode = hepPlanner.findBestExp();
        // 输出优化后的RelNode
        System.out.println("优化后的RelNode: \n" + optimizedRelNode.explain());
        // 使用RelToSqlConverter将优化后的RelNode转换回SQL
        RelToSqlConverter relToSqlConverter = new RelToSqlConverter(MysqlSqlDialect.DEFAULT);
        SqlImplementor.Result result = relToSqlConverter.visitRoot(optimizedRelNode);
        SqlNode sqlNodeConverted = result.asStatement();
        //使用SqlPrettyWriter格式化SQL
        SqlPrettyWriter writer = new SqlPrettyWriter();
        String convertedSql = writer.format(sqlNodeConverted);
        //输出转换后的SQL
        System.out.println("优化后的SQL: " + convertedSql);
    }
}
