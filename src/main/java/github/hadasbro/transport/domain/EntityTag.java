package github.hadasbro.transport.domain;

import javax.persistence.Table;

@SuppressWarnings("unused")
public interface EntityTag extends Printeable{

    int STATUS_ACTIVE = 1;
    int STATUS_INOPERATION = 2;
    int STATUS_INACTIVE = 3;

    static String getTable(Class<?> clazz){
        Table table = clazz.getAnnotation(Table.class);
        if(table == null) {
            return null;
        }
        return table.name();
    }
}
