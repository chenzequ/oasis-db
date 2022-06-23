package cn.oasissoft.core.db.query;

/**
 * @author: Quinn
 * @title: 排序查询条件类
 * @description:
 * @date: 2021-03-19 9:31 下午
 */
public class DbOrderClause {
    public DbOrderClause(String property, DbOrderOperator orderOperator) {
        this.property = property;
        this.orderOperator = orderOperator;
    }

    public DbOrderClause(String property) {
        this(property, DbOrderOperator.Asc);
    }

    private String property;                            //排序属性
    private final DbOrderOperator orderOperator;                 //排序运算符

    /**
     * 获取排序属性
     *
     * @return
     */
    public String getProperty() {
        return property;
    }

    /**
     * 获取排序运算符
     *
     * @return
     */
    public DbOrderOperator getOrderOperator() {
        return orderOperator;
    }
}