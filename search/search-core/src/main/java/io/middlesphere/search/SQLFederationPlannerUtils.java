package io.middlesphere.search;

import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.fun.SqlLibrary;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SQLFederationPlannerUtils {
    private static final int DEFAULT_MATCH_LIMIT = 1024;

    private static final Map<String, SqlLibrary> DATABASE_TYPE_SQL_LIBRARIES = new HashMap<>();

    static {
        DATABASE_TYPE_SQL_LIBRARIES.put("MySQL", SqlLibrary.MYSQL);
        DATABASE_TYPE_SQL_LIBRARIES.put("PostgreSQL", SqlLibrary.POSTGRESQL);
        DATABASE_TYPE_SQL_LIBRARIES.put("openGauss", SqlLibrary.POSTGRESQL);
        DATABASE_TYPE_SQL_LIBRARIES.put("Oracle", SqlLibrary.ORACLE);
    }

    public static RelOptPlanner createVolcanoPlanner() {
        RelOptPlanner result = new VolcanoPlanner();
        setUpRules(result);
        return result;
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
        Collection<RelOptRule> result = new LinkedList<>();
        result.add(CoreRules.FILTER_SUB_QUERY_TO_CORRELATE);
        result.add(CoreRules.PROJECT_SUB_QUERY_TO_CORRELATE);
        result.add(CoreRules.JOIN_SUB_QUERY_TO_CORRELATE);
        return result;
    }


    /**
     * Create rel opt cluster.
     *
     * @param relDataTypeFactory rel data type factory
     * @return rel opt cluster
     */
    public static RelOptCluster createRelOptCluster(final RelDataTypeFactory relDataTypeFactory) {
        return RelOptCluster.create(createVolcanoPlanner(), new RexBuilder(relDataTypeFactory));
    }



}
