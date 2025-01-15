package io.middlesphere.planner.metadata;

import com.google.common.collect.Lists;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.tools.Frameworks;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询原数据信息
 * @author Administrator
 */
public final class SchemaMetaData {
    private final DataSource  dataSource;

    public SchemaMetaData(DataSource  dataSource){
        this.dataSource = dataSource;
    }

    public   SchemaPlus  createRootSchema() throws SQLException {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        try (Connection connection =  dataSource.getConnection();){
            List<TableInfo> tableInfos = Lists.newArrayList();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, null, null);
            while (columns.next()){
                TableInfo tableInfo =new TableInfo();
                String tableSchem  = columns.getString("TABLE_SCHEM");
                tableInfo.setTableSchem(tableSchem);
                String tableName = columns.getString("TABLE_NAME");
                tableInfo.setTableName(tableName);
                String columnName = columns.getString("COLUMN_NAME");
                tableInfo.setColumnName(columnName);
                int dataType = columns.getInt("DATA_TYPE");
                getDataType(tableInfo,dataType);
                tableInfos.add(tableInfo);
            }
            columns.close();
            Map<String,List<TableInfo>> tableSchems = tableInfos.stream().collect(Collectors.groupingBy(TableInfo::getTableSchem));
            for(String tableSchem : tableSchems.keySet()){
                Schema defaultSchema = new AbstractSchema() {};
                rootSchema.add(tableSchem, defaultSchema);
                List<TableInfo> tableInfoList =   tableSchems.get(tableSchem);
                Map<String,List<TableInfo>> tableNames = tableInfos.stream().collect(Collectors.groupingBy(TableInfo::getTableName));
                for(String tableName : tableNames.keySet()){
                    Table table =  createTable(tableName,tableInfoList);
                    rootSchema.getSubSchema(tableSchem).add(tableName,table);
                }
            }
        }
        return rootSchema;
    }


    private   Table  createTable(String tableName,List<TableInfo> tableInfoList){
            Table table = new AbstractTable() {
                @Override
                public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                    // 如果要动态分析表，那么就自己去创建
                    return   getRelDataType(tableInfoList,typeFactory);
                }
            };
            return table;
    }

    private  RelDataType getRelDataType(List<TableInfo> tableInfoList, RelDataTypeFactory typeFactory){
        RelDataTypeFactory.FieldInfoBuilder fieldInfoBuilder =  typeFactory.builder();
        for(TableInfo column : tableInfoList){
            fieldInfoBuilder.add(column.getColumnName(),typeFactory.createJavaType(column.getJavaType()));
        }
        return fieldInfoBuilder.build();
    }

    private  void  getDataType(TableInfo tableInfo,int dataType){
        if(Types.CHAR == dataType || Types.VARCHAR == dataType
                || Types.LONGNVARCHAR==dataType
                || Types.NVARCHAR==dataType){
            tableInfo.setJavaType(String.class);
        }else if(Types.NUMERIC == dataType || Types.DECIMAL == dataType){
            tableInfo.setJavaType(BigDecimal.class);
        }else if(Types.BIT == dataType || Types.BOOLEAN == dataType){
            tableInfo.setJavaType(boolean.class);
        }else if(Types.TINYINT == dataType){
            tableInfo.setJavaType(byte.class);
        }else if(Types.SMALLINT == dataType){
            tableInfo.setJavaType(short.class);
        }else if(Types.INTEGER == dataType){
            tableInfo.setJavaType(int.class);
        }else if(Types.BIGINT == dataType){
            tableInfo.setJavaType(long.class);
        }else if(Types.REAL == dataType){
            tableInfo.setJavaType(float.class);
        }else if(Types.FLOAT == dataType || Types.DOUBLE == dataType){
            tableInfo.setJavaType(double.class);
        }else if(Types.BINARY == dataType || Types.VARBINARY == dataType || Types.LONGVARBINARY == dataType){
            tableInfo.setJavaType(byte[].class);
        }else if(Types.DATE == dataType ){
            tableInfo.setJavaType(Date.class);
        }else if(Types.TIME == dataType ){
            tableInfo.setJavaType(Time.class);
        }else if(Types.TIMESTAMP == dataType ){
            tableInfo.setJavaType(Timestamp.class);
        }else if(Types.CLOB == dataType ){
            tableInfo.setJavaType(Clob.class);
        }else if(Types.BLOB == dataType ){
            tableInfo.setJavaType(Blob.class);
        }else if(Types.ARRAY == dataType ){
            tableInfo.setJavaType(Array.class);
        }else if(Types.REF == dataType ){
            tableInfo.setJavaType(Ref.class);
        }else if(Types.ROWID == dataType ){
            tableInfo.setJavaType(RowId.class);
        }else if(Types.SQLXML == dataType ){
            tableInfo.setJavaType(SQLXML.class);
        }
    }

    public  static  class    TableInfo {
          private  String  tableSchem;

          private  String   tableName;

          private String   columnName;

          private  Class<?>  javaType;

        public String getTableSchem() {
            return tableSchem;
        }

        public void setTableSchem(String tableSchem) {
            this.tableSchem = tableSchem;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public void setJavaType(Class<?> javaType) {
            this.javaType = javaType;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }

}
