package io.middlesphere.planner.rule.transformation;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.rel.rules.TransformationRule;

/**
 * @author Administrator
 */

public class PushFilterIntoScanRule extends RelRule<PushFilterIntoScanRule.Config> implements TransformationRule {


    protected PushFilterIntoScanRule(Config config) {
        super(config);
    }

    @Override
    public void onMatch(RelOptRuleCall relOptRuleCall) {

    }

    public interface Config extends RelRule.Config {


    }
}
