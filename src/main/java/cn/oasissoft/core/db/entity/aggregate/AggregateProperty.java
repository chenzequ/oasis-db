package cn.oasissoft.core.db.entity.aggregate;

import cn.oasissoft.core.db.entity.schema.ColumnSchema;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbDefineException;

/**
 * @author: Quinn
 * @title: 聚合属性类
 * @description:
 * @date: 2021-03-21 8:36 下午
 */
public class AggregateProperty extends AggregatePropertyBase {
    private final String property;

    public AggregateProperty(String property, AggregateOperator op) {
        this(property, op, null);
    }

    public AggregateProperty(String property, AggregateOperator op, Object defaultValue) {
        super(op, defaultValue);
        if (null == property || property.length() == 0) {
            throw new NullPointerException("property");
        }
        this.property = property;
    }

    @Override
    public String getAsName() {
        switch (op) {
            case Sum:
                return property + "_SUM";
            case Count:
                return property + "_COUNT";
            case Avg:
                return property + "_AVG";
            case Max:
                return property + "_MAX";
            case Min:
                return property + "_MIN";
            case Single:
                return property + "_SINGLE";
            default:
                throw new OasisDbDefineException("未实现的聚合运算符");
        }
    }

    @Override
    public String getColumnSqlBy(TableSchema tableSchema) {
        ColumnSchema column = tableSchema.getColumnByProperty(property);
        if (column == null) {
            throw new NullPointerException("无法找到属性[" + property + "]对应的列");
        }
        return column.getColumnNameSql();
    }
}
