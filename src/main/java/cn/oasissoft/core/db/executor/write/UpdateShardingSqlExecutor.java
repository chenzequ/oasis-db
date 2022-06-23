package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.UpdateObject;
import cn.oasissoft.core.db.entity.UpdateSqlObject;
import cn.oasissoft.core.db.entity.schema.ShardingKey;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateFunction;
import cn.oasissoft.core.db.query.DbOperandOperator;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:44
 */
public class UpdateShardingSqlExecutor<T, K> extends ShardingSqlExecutorBase<T, K> {

    private final ExecuteUpdateFunction executeUpdate;

    public UpdateShardingSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, ExecuteUpdateFunction executeUpdate) {
        super(tableSchema, databaseType);
        Assert.notNull(executeUpdate, "executeUpdate is null.");
        this.executeUpdate = executeUpdate;
    }

    private int updatesByIdsBase(List<K> ids, Function<String, Integer> supplier) {
        Set<String> tableNameSet = new HashSet<>();
        for (K id : ids) {
            String tableName = this.tableNameSqlById(id);
            if (!tableNameSet.contains(tableName)) {
                tableNameSet.add(tableName);
            }
        }
        int result = 0;
        for (String tableName : tableNameSet) {
            result += supplier.apply(tableName);
        }

        return result;
    }

    public int by(T model) {
        return this.by(model, null);
    }

    public int by(T model, Set<String> exceptProps) {
        String tableName = this.tableNameSqlByModel(model);
        return WriteSqlExecutorUtils.update(tableName, this.tableSchema, this.databaseType, this.executeUpdate, model, exceptProps);
    }

    public int updates(ShardingKey key, DbQuery query, UpdateObject<T>... updateObjects) {
        String tableName = this.tableNameSqlByShardingKey(key);
        return WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, query, updateObjects);
    }

    public int updates(List<K> ids, UpdateObject<T>... updateObjects) {
        return updatesByIdsBase(ids, tableName -> WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, ids, updateObjects));
    }

    public int updates(ShardingKey key, DbQuery query, UpdateSqlObject<T>... updateObjects) {
        String tableName = this.tableNameSqlByShardingKey(key);
        return WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, query, updateObjects);
    }

    public int updates(List<K> ids, UpdateSqlObject<T>... updateObjects) {
        return updatesByIdsBase(ids, tableName -> WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, ids, updateObjects));
    }

    public int updatesNumber(ShardingKey key, DbQuery query, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
        String tableName = this.tableNameSqlByShardingKey(key);
        return WriteSqlExecutorUtils.updatesNumber(tableName, this.tableSchema, this.databaseType, this.executeUpdate, query, property, op, value, updateObjects);
    }

    public int updatesNumber(ShardingKey key, DbQuery query, String property, DbOperandOperator op, Number value) {
        return this.updatesNumber(key, query, property, op, value, null);
    }

    public int updatesNumber(List<K> ids, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
        return updatesByIdsBase(ids, tableName -> WriteSqlExecutorUtils.updatesNumber(tableName, this.tableSchema, this.databaseType, this.executeUpdate, ids, property, op, value, updateObjects));
    }

    public int updatesNumber(List<K> ids, String property, DbOperandOperator op, Number value) {
        return this.updatesNumber(ids, property, op, value, null);
    }

    public int updatesReplace(ShardingKey key, DbQuery query, String property, String oldValue, String newValue) {
        String tableName = this.tableNameSqlByShardingKey(key);
        return WriteSqlExecutorUtils.updatesReplace(tableName, this.tableSchema, this.databaseType, this.executeUpdate, query, property, oldValue, newValue);
    }

    public int updatesReplace(List<K> ids, String property, String oldValue, String newValue) {
        return updatesByIdsBase(ids, tableName -> WriteSqlExecutorUtils.updatesReplace(tableName, this.tableSchema, this.databaseType, this.executeUpdate, ids, property, oldValue, newValue));
    }

    public int updatesFlip(ShardingKey key, DbQuery query, String property) {
        String tableName = this.tableNameSqlByShardingKey(key);
        return WriteSqlExecutorUtils.updatesValueFlip(tableName, this.tableSchema, this.databaseType, this.executeUpdate, query, property);
    }

    public int updatesFlip(List<K> ids, String property) {
        return updatesByIdsBase(ids, tableName -> WriteSqlExecutorUtils.updatesValueFlip(tableName, this.tableSchema, this.databaseType, this.executeUpdate, ids, property));
    }
}

