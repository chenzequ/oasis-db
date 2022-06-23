package cn.oasissoft.core.db.entity.aggregate;

import cn.oasissoft.core.db.entity.schema.TableSchema;

import java.math.BigDecimal;

/**
 * @author: Quinn
 * @title: 聚合属性 基类
 * @description:
 * @date: 2021-03-21 7:57 下午
 */
public abstract class AggregatePropertyBase {

    protected final AggregateOperator op; // 聚合运算符
    private Object defaultValue; // 默认值

    public AggregatePropertyBase(AggregateOperator op, Object defaultValue) {
        this.op = op;
        this.defaultValue = defaultValue;
        if (null == this.defaultValue) {
            switch (op) {
                case Sum:
                case Avg:
                    this.defaultValue = BigDecimal.ZERO;
                case Count:
                    this.defaultValue = 0L;
                case Min:
                case Max:
                case Single:
                    this.defaultValue = null;
            }
        }
    }

    /**
     * 获取聚合运算符
     *
     * @return
     */
    public AggregateOperator getOp() {
        return op;
    }

    /**
     * 获取默认值
     *
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    public abstract String getAsName();

    public abstract String getColumnSqlBy(TableSchema tableSchema);
}
