package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.ExecuteBatchUpdateFunction;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateAutoIncrementFunction;
import cn.oasissoft.core.db.query.LambdaFunction;
import cn.oasissoft.core.db.utils.LambdaUtils;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插入sql执行器(单表)
 *
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:04
 */
public class InsertSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final ExecuteUpdateAutoIncrementFunction executeUpdateAutoIncrement;
    private final ExecuteBatchUpdateFunction executeBatchUpdate;

    public InsertSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, ExecuteUpdateAutoIncrementFunction executeUpdateAutoIncrement, ExecuteBatchUpdateFunction executeBatchUpdate) {
        super(tableSchema, databaseType);
        Assert.notNull(executeUpdateAutoIncrement, "executeUpdateAutoIncrement is null");
        this.executeUpdateAutoIncrement = executeUpdateAutoIncrement;
        Assert.notNull(executeBatchUpdate, "executeBatchUpdate is null");
        this.executeBatchUpdate = executeBatchUpdate;
    }

    // 公有方法

    /**
     * 保存
     *
     * @param model
     * @return
     */
    public int by(T model) {
        return this.by(model, (Set<String>) null);
    }

    /**
     * 保存
     *
     * @param model
     * @param exceptProps
     * @return
     */
    public int by(T model, Set<String> exceptProps) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.insert(tableName, tableSchema, databaseType, executeUpdateAutoIncrement, model, exceptProps);
    }

    public int by(T model, LambdaFunction<T>... exceptProps) {
        if (exceptProps == null || exceptProps.length == 0) {
            return by(model);
        } else {
            Set<String> props = new HashSet<>(exceptProps.length);
            for (LambdaFunction<T> lambdaFunction : exceptProps) {
                props.add(LambdaUtils.getPropertyName(lambdaFunction));
            }
            return by(model, exceptProps);
        }
    }


    /**
     * 批量保存
     *
     * @param models
     * @param exceptProps
     * @return
     */
    public int[] batchSave(List<T> models, Set<String> exceptProps) {
        String tableName = this.tableSchema.getTableNameSql();
        return WriteSqlExecutorUtils.batchInsert(tableName, this.tableSchema, this.databaseType, executeBatchUpdate, models, exceptProps);
    }

    /**
     * 批量保存
     *
     * @param model
     * @return
     */
    public int[] batchSave(List<T> model) {
        return this.batchSave(model, null);
    }

//    /**
//     * 新增或更新
//     * 暂不实现:
//     * mysql: 1. replace into..
//     * sqlserver: if ...
//     * oracle merge into...
//     * @param model
//     * @param exceptProps
//     * @return
//     */
//    public int saveOrUpdate(T model, Set<String> exceptProps) {
//
//    }
//
//    /**
//     * 新增或更新
//     * @param model
//     * @return
//     */
//    public int saveOrUpdate(T model) {
//        return this.saveOrUpdate(model, null);
//    }
}
