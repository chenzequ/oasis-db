package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:44
 */
public class DeleteSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final ExecuteUpdateFunction executeUpdateFunction;

    public DeleteSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, ExecuteUpdateFunction executeUpdateFunction) {
        super(tableSchema, databaseType);
        Assert.notNull(executeUpdateFunction, "executeUpdateFunction is null.");
        this.executeUpdateFunction = executeUpdateFunction;
    }

    // 私有方法

    // 公有方法

    public int byId(K id) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.delete(tableName, this.tableSchema, this.databaseType, this.executeUpdateFunction, id);
    }

    public int byQuery(DbQuery query) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.delete(tableName, this.tableSchema, this.databaseType, this.executeUpdateFunction, query);
    }
}
