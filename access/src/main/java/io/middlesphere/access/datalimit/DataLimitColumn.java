package io.middlesphere.access.datalimit;

/**
 * @author Administrator
 */
public class DataLimitColumn {
    private  String  name;
    private  String  op;
    private  Object  value;

    private  String  mode;

    public DataLimitColumn(String name, String op, String value) {
        this.name = name;
        this.op = op;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getOp() {
        return op;
    }

    public Object getValue() {
        return value;
    }


    public void setValue(Object value) {
        this.value = value;
    }
}
