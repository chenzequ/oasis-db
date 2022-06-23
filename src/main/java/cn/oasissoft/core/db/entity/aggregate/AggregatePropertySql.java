package cn.oasissoft.core.db.entity.aggregate;

import cn.oasissoft.core.db.entity.schema.TableSchema;

/**
 * @author: Quinn
 * @title: 聚合属性(名称为sql) 类
 * @description:
 * 例如: SUM(a.f_name) AS name
 *      SUM(f_price * f_unit) AS total
 * @date: 2021-03-21 8:36 下午
 */
public class AggregatePropertySql extends AggregatePropertyBase {

    private final String fieldSql;  // 聚合函数中间的sql
    private final String asName; // 聚合函数别名

    public AggregatePropertySql(String fieldSql, String asName, AggregateOperator op, Object defaultValue) {
        super(op, defaultValue);

        if (null == fieldSql || fieldSql.length() == 0) {
            throw new NullPointerException("fieldSql");
        }

        if (null == asName || asName.length() == 0) {
            throw new NullPointerException("asName");
        }

        this.fieldSql = fieldSql;
        this.asName = asName;
    }

    @Override
    public String getAsName() {
        return asName;
    }

    @Override
    public String getColumnSqlBy(TableSchema tableSchema) {
        return fieldSql;
    }

}
