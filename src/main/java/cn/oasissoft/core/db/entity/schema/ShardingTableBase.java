package cn.oasissoft.core.db.entity.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 分表表名策略基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/22 13:20
 */
public abstract class ShardingTableBase<T, K, V> implements ShardingTable<T, K, V> {

    // 获取分表表名序号
    protected abstract String getShardingIndexBy(V key);

    protected abstract V getKeyByModel(T model);

    protected abstract V getKeyById(K id);

    protected String getTableName(String baseTableName, V key) {
        String indexName = getShardingIndexBy(key);
        return String.format("`%s_%s`", baseTableName, indexName);
    }

    @Override
    public String getTableNameByModel(String baseTableName, T model) {
        V key = getKeyByModel(model);
        return getTableName(baseTableName, key);
    }

    @Override
    public String getTableNameById(String baseTableName, K id) {
        V key = getKeyById(id);
        return getTableName(baseTableName, key);
    }

    @Override
    public String getTableNameByKey(String baseTableName, ShardingKeys<V> shardingKey) {
        Set<V> keys = shardingKey.getKeys();
        if (keys.size() == 1) {
            return getTableName(baseTableName, keys.stream().findFirst().get());
        } else {
            List<String> tableNames = new ArrayList<>(keys.size());
            for (V key : keys) {
                tableNames.add(getTableName(baseTableName, key));
            }

            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < tableNames.size(); i++) {
                if (i != 0) {
                    sb.append(" UNION ");
                }
                sb.append(" SELECT * FROM `" + tableNames.get(i) + "`");
            }
            sb.append(") AS ").append(baseTableName);
            return sb.toString();
        }
    }
}
