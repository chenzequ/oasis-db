package cn.oasissoft.core.db.executor.ddl;

/**
 * 数据库索引对象
 */
public final class DBIndex {

    public DBIndex(IndexType type, int length) {
        this.type = type;
        this.length = length;
    }

    /**
     * 列 索引
     */
    public enum IndexType {
        // 普通索引
        INDEX,
        // 唯一索引
        UNIQUE
    }

    private final IndexType type;
    private final int length;

    /**
     * 索引类型
     *
     * @return
     */
    public IndexType getType() {
        return type;
    }

    /**
     * 索引长度
     *
     * @return
     */
    public int getLength() {
        return length;
    }
}
