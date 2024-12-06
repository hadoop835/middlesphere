package io.middlesphere.access.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.middlesphere.access.datalimit.DataLimitDatabase;

/**
 * @author Administrator
 */
public final class DataLimitContext {

    private final static TransmittableThreadLocal<DataLimitDatabase> LOCAL = new TransmittableThreadLocal<DataLimitDatabase>();


    public static DataLimitDatabase getDataLimitContext(){
           return LOCAL.get();
    }

    public static void clear(){
        LOCAL.remove();
    }

    public static void setDataLimitContext(DataLimitDatabase dataLimitDatabase){
         LOCAL.set(dataLimitDatabase);
    }


}
