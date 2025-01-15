package io.middlesphere.planner.rule.converter;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Administrator
 */
public class EnumerableScanConverterRule extends ConverterRule {
    public static final Config DEFAULT_CONFIG = Config.INSTANCE.withConversion(null, Convention.NONE, EnumerableConvention.INSTANCE, EnumerableScanConverterRule.class.getSimpleName())
            .withRuleFactory(EnumerableScanConverterRule::new);

    protected EnumerableScanConverterRule(Config config) {
        super(config);
    }

    @Override
    public @Nullable RelNode convert(RelNode relNode) {
        return null;
    }
}
