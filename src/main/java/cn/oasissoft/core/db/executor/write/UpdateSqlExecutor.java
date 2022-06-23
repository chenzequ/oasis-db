package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.UpdateObject;
import cn.oasissoft.core.db.entity.UpdateSqlObject;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateFunction;
import cn.oasissoft.core.db.query.DbOperandOperator;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:44
 */
public class UpdateSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final ExecuteUpdateFunction executeUpdate;

    public UpdateSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, ExecuteUpdateFunction executeUpdate) {
        super(tableSchema, databaseType);
        Assert.notNull(executeUpdate, "executeUpdate is null.");
        this.executeUpdate = executeUpdate;
    }


    public int by(T model) {
        return this.by(model, null);
    }

    public int by(T model, Set<String> exceptProps) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.update(tableName, this.tableSchema, this.databaseType, this.executeUpdate, model, exceptProps);
    }

    public int updates(DbQuery query, UpdateObject<T>... updateObjects) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, query, updateObjects);
    }

    public int updates(List<K> ids, UpdateObject<T>... updateObjects) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, ids, updateObjects);
    }

    public int updates(DbQuery query, UpdateSqlObject<T>... updateObjects) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, query, updateObjects);
    }

    public int updates(List<K> ids, UpdateSqlObject<T>... updateObjects) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updates(tableName, this.tableSchema, this.databaseType, executeUpdate, ids, updateObjects);
    }

    public int updatesNumber(DbQuery query, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updatesNumber(tableName, this.tableSchema, this.databaseType, this.executeUpdate, query, property, op, value, updateObjects);
    }

    public int updatesNumber(DbQuery query, String property, DbOperandOperator op, Number value) {
        return this.updatesNumber(query, property, op, value, null);
    }

    public int updatesNumber(List<K> ids, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updatesNumber(tableName, this.tableSchema, this.databaseType, this.executeUpdate, ids, property, op, value, updateObjects);
    }

    public int updatesNumber(List<K> ids, String property, DbOperandOperator op, Number value) {
        return this.updatesNumber(ids, property, op, value, null);
    }

    public int updatesReplace(DbQuery query, String property, String oldValue, String newValue) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updatesReplace(tableName, this.tableSchema, this.databaseType, this.executeUpdate, query, property, oldValue, newValue);
    }

    public int updatesReplace(List<K> ids, String property, String oldValue, String newValue) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updatesReplace(tableName, this.tableSchema, this.databaseType, this.executeUpdate, ids, property, oldValue, newValue);
    }

    public int updatesFlip(DbQuery query, String property) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updatesValueFlip(tableName, this.tableSchema, this.databaseType, this.executeUpdate, query, property);
    }

    public int updatesFlip(List<K> ids, String property) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.updatesValueFlip(tableName, this.tableSchema, this.databaseType, this.executeUpdate, ids, property);
    }
}

