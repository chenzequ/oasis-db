package cn.oasissoft.core.db.entity.schema;

/**
 * 不分表
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 18:28
 */
public class NoneShardingTable implements ShardingTable {

    public static final NoneShardingTable INSTANCE = new NoneShardingTable();


    @Override
    public String getTableNameByModel(String baseTableName, Object model) {
        return baseTableName;
    }

    @Override
    public String getTableNameById(String baseTableName, Object id) {
        return baseTableName;
    }

    @Override
    public String getTableNameByKey(String baseTableName, ShardingKeys shardingKey) {
        return baseTableName;
    }

}
