package io.middlesphere.access.datalimit;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Administrator
 */
public class DataLimitTable {
    private  String  name;

    private List<DataLimitColumn> dataLimitColumns = Lists.newArrayList();



    public   void  addColumn(DataLimitColumn column){
        if(!dataLimitColumns.contains(column)){
            dataLimitColumns.add(column);
        }
    }

    public String getName() {
        return name;
    }

    public List<DataLimitColumn> getDataLimitColumns() {
        return dataLimitColumns;
    }

    public void setName(String name) {
        this.name = name;
    }

}
