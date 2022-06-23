package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.schema.ShardingKeys;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QueryForListFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 17:22
 */
public class QueryListShardingSqlExecutor<T, K> extends ShardingSqlExecutorBase<T, K> {

    private final QueryForListFunction queryForList;

    public QueryListShardingSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QueryForListFunction queryForList) {
        super(tableSchema, databaseType);
        Assert.notNull(queryForList, "queryForList not null");
        this.queryForList = queryForList;
    }

    public List<T> toModels(ShardingKeys keys, DbQuery query) {
        return toModels(keys, query, -1, 1);
    }

    public List<T> toModels(ShardingKeys keys, DbQuery query, int size, int index) {
        String tableName = this.tableNameSqlByShardingKeys(keys);
        return QuerySqlExecutorUtils.queryModels(tableName, this.tableSchema, this.databaseType, this.queryForList, query, size, index);
    }

    public <V> List<V> toViews(Class<V> vClass, ShardingKeys keys, DbQuery query) {
        return toViews(vClass, keys, query, -1, 1);
    }

    public <V> List<V> toViews(Class<V> vClass, ShardingKeys keys, DbQuery query, int size, int index) {
        String tableName = this.tableNameSqlByShardingKeys(keys);
        return QuerySqlExecutorUtils.queryViews(vClass, tableName, this.tableSchema, this.databaseType, this.queryForList, query, size, index);
    }

    public List<MapEntity> toMaps(Set<String> props, ShardingKeys keys, DbQuery query) {
        return toMaps(props, keys, query, -1, 1);
    }

    public List<MapEntity> toMaps(Set<String> props, ShardingKeys keys, DbQuery query, int size, int index) {
        String tableName = this.tableNameSqlByShardingKeys(keys);
        return QuerySqlExecutorUtils.queryMaps(props, tableName, this.tableSchema, this.databaseType, this.queryForList, query, size, index);
    }

}
