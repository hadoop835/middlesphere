package io.middlesphere.search;



import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.calcite.plan.*;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class Elasticsearch {

    private  String name;
    public static void main(String[] args) throws SqlParseException, RelConversionException, ValidationException {
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
                .defaultSchema(rootSchema)
                .build();
        Planner planner =  Frameworks.getPlanner(frameworkConfig);

        SqlParser.Config config = SqlParser.configBuilder().setCaseSensitive(false).build();

        SqlParser parser = SqlParser.create("SELECT\n" +
                "            af.customer_no customerCode,\n" +
                "            af.trade_id businessNo,\n" +
                "            af.trade_amount taxprice,\n" +
                "            af.change_type billType,\n" +
                "            ifnull( af.business_order_no, af.order_no ) orderNo,\n" +
                "            af.business_date businessDate,\n" +
                "            af.create_person createPerson,\n" +
                "            af.remark systemRemark,\n" +
                "            CONCAT_WS( '', ao.remark, ad.remark ) remark,\n" +
                "            ext.cost_belong_period costBelongPeriod,\n" +
                "            (CASE WHEN ifnull( ext.cost_belong_period,'') =''  THEN DATE_FORMAT( af.business_date, '%Y-%m-%d %H:%i:%s' ) ELSE concat( SUBSTR( ext.cost_belong_period, 1, 7 ), '-01 12:00:00' ) END  ) costBelongTime\n" +
                "        FROM\n" +
                "            st_account_flow af\n" +
                "                INNER JOIN st_account a ON af.account_id = a.id AND a.dr = 0\n" +
                "                LEFT JOIN st_account_augment_order ao ON af.business_order_no = ao.apply_order_no AND ao.dr = 0\n" +
                "                LEFT JOIN st_account_deduction_order ad ON af.business_order_no = ad.apply_order_no AND ad.dr = 0\n" +
                "                LEFT JOIN dd_account_cost_ext ext ON IFNULL( ao.id, ad.id )= ext.source_id AND ext.source_code IN ( 'ACCOUNT_AUGMENT', 'ACCOUNT_DEDUCTION','REBATE_BILL')  and  ext.dr=0\n" +
                "        WHERE af.dr = 0\n" +
                "          and af.change_type  in('05','06','08','09','10','13','37','52')\n" +
                "          AND a.parent_account_type IN ( '200003', '200002' )", config);

        SqlNode sqlNode = parser.parseStmt();
        RelNode relNode = planner.rel(sqlNode).rel;



        System.out.println(relNode.explain());

//        SQLExpr sqlExpr =  SQLUtils.toMySqlExpr("SELECT\n" +
//                "            af.customer_no customerCode,\n" +
//                "            af.trade_id businessNo,\n" +
//                "            af.trade_amount taxprice,\n" +
//                "            af.change_type billType,\n" +
//                "            ifnull( af.business_order_no, af.order_no ) orderNo,\n" +
//                "            af.business_date businessDate,\n" +
//                "            af.create_person createPerson,\n" +
//                "            af.remark systemRemark,\n" +
//                "            CONCAT_WS( '', ao.remark, ad.remark ) remark,\n" +
//                "            ext.cost_belong_period costBelongPeriod,\n" +
//                "            (CASE WHEN ifnull( ext.cost_belong_period,'') =''  THEN DATE_FORMAT( af.business_date, '%Y-%m-%d %H:%i:%s' ) ELSE concat( SUBSTR( ext.cost_belong_period, 1, 7 ), '-01 12:00:00' ) END  ) costBelongTime\n" +
//                "        FROM\n" +
//                "            st_account_flow af\n" +
//                "                INNER JOIN st_account a ON af.account_id = a.id AND a.dr = 0\n" +
//                "                LEFT JOIN st_account_augment_order ao ON af.business_order_no = ao.apply_order_no AND ao.dr = 0\n" +
//                "                LEFT JOIN st_account_deduction_order ad ON af.business_order_no = ad.apply_order_no AND ad.dr = 0\n" +
//                "                LEFT JOIN dd_account_cost_ext ext ON IFNULL( ao.id, ad.id )= ext.source_id AND ext.source_code IN ( 'ACCOUNT_AUGMENT', 'ACCOUNT_DEDUCTION','REBATE_BILL')  and  ext.dr=0\n" +
//                "        WHERE af.dr = 0\n" +
//                "          and af.change_type  in('05','06','08','09','10','13','37','52')\n" +
//                "          AND a.parent_account_type IN ( '200003', '200002' )");



//
//         SQLExpr sqlExpr =  SQLUtils.toMySqlExpr("select * from user user ");
//        Set<SQLExprTableSource> sqlExprTableSources = Sets.newHashSet();
//       // SQLExpr w = SQLUtils.toSQLExpr(" name=1 and status in(1,2)");
//        //System.out.println(w);
//
//        Set<SQLExpr> sqlBinaryOpExprs = Sets.newHashSet();
//        if( sqlExpr instanceof SQLQueryExpr sqlQueryExpr){
//            SQLSelect sqlSelect =  sqlQueryExpr.getSubQuery();
//            SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
//           if(sqlSelectQuery instanceof  MySqlSelectQueryBlock queryBlock){
//               SQLTableSource sqlTableSource =  queryBlock.getFrom();
//               toSQLTableSource(sqlTableSource,sqlExprTableSources);
//               //处理where条件
//               SQLBinaryOpExpr sqlExpr1 =  (SQLBinaryOpExpr)queryBlock.getWhere();
//
//               toSQLBinaryOpExpr(sqlExpr1,sqlBinaryOpExprs);
//
//
//               SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
//               sqlBinaryOpExpr.setOperator(SQLBinaryOperator.Equality);
//               sqlBinaryOpExpr.setLeft(new SQLIdentifierExpr("dr"));
//               sqlBinaryOpExpr.setRight(new SQLIntegerExpr(1));
//               ((SQLBinaryOpExpr) queryBlock.getWhere()).setRight(sqlBinaryOpExpr);
//               System.out.println(sqlExpr1);
//           }
//
//        }

    }


    private static   void   toSQLTableSource(SQLTableSource sqlTableSource,Set<SQLExprTableSource> sqlExprTableSources){
             if(sqlTableSource instanceof SQLExprTableSource sqlExprTableSource){
                 sqlExprTableSources.add(sqlExprTableSource);
             } else if (sqlTableSource instanceof SQLJoinTableSource sqlJoinTableSource) {
                 toSQLTableSource(sqlJoinTableSource.getLeft(),sqlExprTableSources);
                 toSQLTableSource(sqlJoinTableSource.getRight(),sqlExprTableSources);
             }
    }

    private static   void   toSQLBinaryOpExpr(SQLBinaryOpExpr sqlBinaryOpExpr,Set<SQLExpr> sqlExprs){
        SQLExpr sqlExpr =  sqlBinaryOpExpr.getLeft();
        if(sqlExpr instanceof  SQLBinaryOpExpr left){
            sqlExprs.add(sqlBinaryOpExpr.getRight());
            toSQLBinaryOpExpr(left,sqlExprs);
        }else{
            sqlExprs.add(sqlBinaryOpExpr);
        }
    }



}
