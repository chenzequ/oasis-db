package cn.oasissoft.core.db.entity.schema;

/**
 * 分表
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 17:55
 */
public interface ShardingTable<T, K, V> {

    /**
     * 根据模型获取分表表名
     *
     * @param baseTableName
     * @param model
     * @return
     */
    String getTableNameByModel(String baseTableName, T model);

    /**
     * 根据主键获取分表表名
     *
     * @param baseTableName
     * @param id
     * @return
     */
    String getTableNameById(String baseTableName, K id);

    /**
     * 根据分表关键字获取分表表名
     *
     * @param baseTableName
     * @param shardingKey
     * @return
     */
    String getTableNameByKey(String baseTableName, ShardingKeys<V> shardingKey);
}
