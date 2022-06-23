package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.ShardingKeys;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:44
 */
public class DeleteShardingSqlExecutor<T, K> extends ShardingSqlExecutorBase<T, K> {

    private final ExecuteUpdateFunction executeUpdateFunction;

    public DeleteShardingSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, ExecuteUpdateFunction executeUpdateFunction) {
        super(tableSchema, databaseType);
        Assert.notNull(executeUpdateFunction, "executeUpdateFunction is null.");
        this.executeUpdateFunction = executeUpdateFunction;
    }

    // 私有方法

    // 公有方法

    public int byId(K id) {
        String tableName = this.tableNameSqlById(id);
        return WriteSqlExecutorUtils.delete(tableName, this.tableSchema, this.databaseType, this.executeUpdateFunction, id);
    }

    public int byQuery(ShardingKeys keys, DbQuery query) {
        String tableName = this.tableNameSqlByShardingKeys(keys);
        return WriteSqlExecutorUtils.delete(tableName, this.tableSchema, this.databaseType, this.executeUpdateFunction, query);
    }
}
