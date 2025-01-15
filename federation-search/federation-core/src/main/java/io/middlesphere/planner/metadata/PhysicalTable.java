package io.middlesphere.planner.metadata;

import com.google.common.collect.Lists;
import org.apache.calcite.schema.Table;

import java.util.List;

public final class PhysicalTable {
    private  String  tableSchem;

    private List<Table>  tables = Lists.newArrayList();

    public PhysicalTable(String tableSchem) {
        this.tableSchem = tableSchem;
    }


    public String getTableSchem() {
        return tableSchem;
    }

    public List<Table> getTables() {
        return tables;
    }

    public  void  addTable(Table table){
        if(!tables.contains(table)){
            tables.add(table);
        }
    }
}
