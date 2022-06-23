package cn.oasissoft.core.db.executor;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import org.springframework.util.Assert;

/**
 * sql扩展执行器基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 16:27
 */
public abstract class SqlExecutorBase<T, K> {

    protected final TableSchema<T> tableSchema;
    protected final DatabaseType databaseType;

    protected SqlExecutorBase(TableSchema<T> tableSchema, DatabaseType databaseType) {
        Assert.notNull(tableSchema, "schema is null");
        this.tableSchema = tableSchema;
        this.databaseType = databaseType;
    }





}
