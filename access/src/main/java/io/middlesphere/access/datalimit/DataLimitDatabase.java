package io.middlesphere.access.datalimit;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author Administrator
 */
public class DataLimitDatabase {
    private Set<DataLimitTable> dataLimitTables = Sets.newHashSet();

    public Set<DataLimitTable> getDataLimitTables() {
        return dataLimitTables;
    }
}
