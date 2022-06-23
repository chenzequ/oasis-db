package cn.oasissoft.core.db.entity.schema;

/**
 * 列名与属性名的转换器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 14:25
 */
public interface ColumnNameConverter {

    /**
     * 属性名转列名
     * @param property
     * @return
     */
    String toColumnName(String property);
}
