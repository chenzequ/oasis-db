package cn.oasissoft.core.db.executor;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.ShardingKey;
import cn.oasissoft.core.db.entity.schema.ShardingKeys;
import cn.oasissoft.core.db.entity.schema.TableSchema;

/**
 * 分表sql执行器 基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/21 11:52
 */
public abstract class ShardingSqlExecutorBase<T, K> extends SqlExecutorBase<T, K> {
    protected ShardingSqlExecutorBase(TableSchema<T> tableSchema, DatabaseType databaseType) {
        super(tableSchema, databaseType);
    }

    protected String tableNameSqlByShardingKey(ShardingKey shardingKey) {
        return this.tableNameSqlByShardingKeys(shardingKey.toKeys());
    }

    protected String tableNameSqlByShardingKeys(ShardingKeys shardingKeys) {
        return this.tableSchema.getTableNameSql(this.tableSchema.getShardingTable().getTableNameByKey(this.tableSchema.getTableName(), shardingKeys));
    }

    protected String tableNameSqlById(K id) {
        return this.tableSchema.getTableNameSql(this.tableSchema.getShardingTable().getTableNameById(this.tableSchema.getTableName(), id));
    }

    protected String tableNameSqlByModel(T model) {
        return this.tableSchema.getTableNameSql(this.tableSchema.getShardingTable().getTableNameByModel(this.tableSchema.getTableName(), model));
    }

}
