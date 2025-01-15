package io.middlesphere.search;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws SQLException, SqlParseException {
        // 创建一个内存中的 schema
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema.add("HR", new ReflectiveSchema(new HrSchema()));

        // 配置 Calcite 连接属性
        Properties info = new Properties();
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);

        // 设置 schema
        calciteConnection.getRootSchema().add("HR", rootSchema.getSubSchema("HR"));

    }

    public static class HrSchema {
        public final Employee[] emps = {
                new Employee(1, "Alice", 10),
                new Employee(2, "Bob", 20),
                new Employee(3, "Charlie", 20)
        };
    }

    public static class Employee {
        public final int empid;
        public final String name;
        public final int deptno;

        public Employee(int empid, String name, int deptno) {
            this.empid = empid;
            this.name = name;
            this.deptno = deptno;
        }
    }
    }

