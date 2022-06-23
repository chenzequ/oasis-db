package cn.oasissoft.core.db.executor.ddl;

/**
 * @author: Quinn
 * @title: 数据库组合索引对象类
 * @description: 组合索引可以使用主键字段，但是不建议
 * @date: 2021/11/20
 */
public class DBCombineIndex {

    // 索引属性
    private final String property;
    // 属性长度
    private final int length;

    public DBCombineIndex(String property, int length) {
        this.property = property;
        this.length = length;
    }
    public DBCombineIndex(String property) {
        this(property, -1);
    }

    public String getProperty() {
        return property;
    }

    public int getLength() {
        return length;
    }
}
