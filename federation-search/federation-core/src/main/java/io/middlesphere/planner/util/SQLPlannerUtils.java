package io.middlesphere.planner.util;

import com.google.common.collect.Lists;
import io.middlesphere.planner.sql.SQLDialectFactory;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.hep.HepMatchOrder;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.rules.AggregateExpandDistinctAggregatesRule;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.rules.ProjectRemoveRule;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.util.SqlString;
import org.apache.calcite.tools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Administrator
 */
public final class SQLPlannerUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(SQLPlannerUtils.class);
    private SQLPlannerUtils(){}

    private static final int DEFAULT_MATCH_LIMIT = 1024;


    private  static  SchemaPlus  SCHEMA_PLUS = null ;


    public  static  void init(final SchemaPlus schemaPlus){
        SCHEMA_PLUS = schemaPlus;
    }

    /**
     * Create new instance of volcano planner.
     *
     * @return volcano planner instance
     */
    public static RelOptPlanner createVolcanoPlanner() {
        RelOptPlanner result = new VolcanoPlanner();
        setUpRules(result);
        return result;
    }

    /**
     * Create new instance of hep planner.
     *
     * @return hep planner instance
     */
    private static HepPlanner createHepPlanner() {
        HepProgramBuilder builder = new HepProgramBuilder();
        builder.addGroupBegin().addRuleCollection(getFilterRules()).addGroupEnd().addMatchOrder(HepMatchOrder.BOTTOM_UP);
        builder.addGroupBegin().addRuleCollection(getProjectRules()).addGroupEnd().addMatchOrder(HepMatchOrder.BOTTOM_UP);
        builder.addGroupBegin().addRuleCollection(getAggregationRules()).addGroupEnd().addMatchOrder(HepMatchOrder.BOTTOM_UP);
        builder.addGroupBegin().addRuleCollection(getCalcRules()).addGroupEnd().addMatchOrder(HepMatchOrder.BOTTOM_UP);
        builder.addGroupBegin().addRuleCollection(getSubQueryRules()).addGroupEnd().addMatchOrder(HepMatchOrder.BOTTOM_UP);
        builder.addMatchLimit(DEFAULT_MATCH_LIMIT);
        return new HepPlanner(builder.build());
    }

    /**
     *
     * @param sql
     * @param databaseType
     * @return
     * @throws SqlParseException
     * @throws ValidationException
     * @throws RelConversionException
     */
    public static   String  parse(String sql, String databaseType,boolean format) throws SqlParseException, ValidationException, RelConversionException {
        final FrameworkConfig config = Frameworks.newConfigBuilder()
                  .defaultSchema(SCHEMA_PLUS)
                  .build();
        Planner planner = Frameworks.getPlanner(config);
        SqlNode sqlNode =  planner.parse(sql);
        SqlNode validatedSqlNode = planner.validate(sqlNode);
        RelRoot relRoot = planner.rel(validatedSqlNode);
        RelNode logicalPlan = relRoot.rel;
        RelNode optimizedRelNode  = rewrite(logicalPlan);
        return createSQLString(optimizedRelNode,databaseType,format).getSql();
    }

    /**
     *
     * @param logicalPlan
     * @return
     */
    private static RelNode rewrite(final RelNode logicalPlan) {
        HepPlanner hepPlanner = createHepPlanner();
        hepPlanner.setRoot(logicalPlan);
        return hepPlanner.findBestExp();
    }

    private static void setUpRules(final RelOptPlanner planner) {
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        planner.addRelTraitDef(RelCollationTraitDef.INSTANCE);
        planner.addRule(EnumerableRules.ENUMERABLE_JOIN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_CORRELATE_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_CALC_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_AGGREGATE_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_LIMIT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_COLLECT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_UNCOLLECT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_UNION_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_REPEAT_UNION_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_TABLE_SPOOL_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_INTERSECT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_MINUS_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_WINDOW_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_TABLE_FUNCTION_SCAN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_MATCH_RULE);
    }

    private static Collection<RelOptRule> getSubQueryRules() {
        Collection<RelOptRule> result = Lists.newLinkedList();
        result.add(CoreRules.FILTER_SUB_QUERY_TO_CORRELATE);
        result.add(CoreRules.PROJECT_SUB_QUERY_TO_CORRELATE);
        result.add(CoreRules.JOIN_SUB_QUERY_TO_CORRELATE);
        return result;
    }

    private static Collection<RelOptRule> getCalcRules() {
        Collection<RelOptRule> result = Lists.newLinkedList();
        result.add(AggregateExpandDistinctAggregatesRule.Config.DEFAULT.toRule());
        result.add(CoreRules.PROJECT_TO_CALC);
        result.add(CoreRules.FILTER_TO_CALC);
        result.add(CoreRules.PROJECT_CALC_MERGE);
        result.add(CoreRules.FILTER_CALC_MERGE);
        result.add(EnumerableRules.ENUMERABLE_FILTER_TO_CALC_RULE);
        result.add(EnumerableRules.ENUMERABLE_PROJECT_TO_CALC_RULE);
        result.add(CoreRules.CALC_MERGE);
        return result;
    }

    private static Collection<RelOptRule> getProjectRules() {
        Collection<RelOptRule> result = Lists.newLinkedList();
        result.add(CoreRules.PROJECT_MERGE);
        result.add(CoreRules.PROJECT_CORRELATE_TRANSPOSE);
        result.add(CoreRules.PROJECT_SET_OP_TRANSPOSE);
        result.add(CoreRules.PROJECT_JOIN_TRANSPOSE);
        result.add(CoreRules.PROJECT_REDUCE_EXPRESSIONS);
        result.add(ProjectRemoveRule.Config.DEFAULT.toRule());
        return result;
    }

    private static Collection<RelOptRule> getFilterRules() {
        Collection<RelOptRule> result = Lists.newLinkedList();
        result.add(CoreRules.FILTER_INTO_JOIN);
        result.add(CoreRules.JOIN_CONDITION_PUSH);
        result.add(CoreRules.SORT_JOIN_TRANSPOSE);
        result.add(CoreRules.FILTER_AGGREGATE_TRANSPOSE);
        result.add(CoreRules.FILTER_PROJECT_TRANSPOSE);
        result.add(CoreRules.FILTER_SET_OP_TRANSPOSE);
        result.add(CoreRules.FILTER_REDUCE_EXPRESSIONS);
        result.add(CoreRules.FILTER_MERGE);
        result.add(CoreRules.JOIN_PUSH_EXPRESSIONS);
        result.add(CoreRules.JOIN_PUSH_TRANSITIVE_PREDICATES);
        return result;
    }

    private static Collection<RelOptRule> getAggregationRules() {
        Collection<RelOptRule> result = Lists.newLinkedList();
        result.add(CoreRules.AGGREGATE_MERGE);
        result.add(CoreRules.AGGREGATE_REDUCE_FUNCTIONS);
        return result;
    }

    /**
     * 创建sql转化
     * @param scanContext
     * @param databaseType
     * @return
     */
    private static SqlString createSQLString(final RelNode scanContext, final String databaseType,boolean format) {
        SqlDialect sqlDialect = SQLDialectFactory.getSQLDialect(databaseType);
        SqlNode sqlNodeConverted =  new RelToSqlConverter(sqlDialect).visitRoot(scanContext).asStatement();
        if(format){
            SqlPrettyWriter writer = new SqlPrettyWriter();
            String convertedSql = writer.format(sqlNodeConverted);
            LOGGER.info("优化后的SQL:{}",convertedSql);
        }
        return sqlNodeConverted.toSqlString(sqlDialect);
    }

}
