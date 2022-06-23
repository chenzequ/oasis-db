package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
import cn.oasissoft.core.db.executor.function.ExecuteBatchUpdateFunction;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateAutoIncrementFunction;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 插入sql执行器(单表)
 *
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:04
 */
public class InsertShardingSqlExecutor<T, K> extends ShardingSqlExecutorBase<T, K> {

    private final ExecuteUpdateAutoIncrementFunction executeUpdateAutoIncrement;
    private final ExecuteBatchUpdateFunction executeBatchUpdate;

    public InsertShardingSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, ExecuteUpdateAutoIncrementFunction executeUpdateAutoIncrement, ExecuteBatchUpdateFunction executeBatchUpdate) {
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
        return this.by(model, null);
    }

    /**
     * 保存
     *
     * @param model
     * @param exceptProps
     * @return
     */
    public int by(T model, Set<String> exceptProps) {
        String tableName = this.tableNameSqlByModel(model);
        return WriteSqlExecutorUtils.insert(tableName, tableSchema, databaseType, executeUpdateAutoIncrement, model, exceptProps);
    }

    /**
     * 批量保存
     *
     * @param models
     * @param exceptProps
     * @return
     */
    public int[] batchSave(List<T> models, Set<String> exceptProps) {

        Map<String, List<T>> map = new HashMap<>();
        Map<String, List<Integer>> indexMap = new HashMap<>();
        for (int i = 0; i < models.size(); i++) {
            T model = models.get(i);
            String tableName = this.tableNameSqlByModel(model);
            if (!map.containsKey(tableName)) {
                map.put(tableName, new ArrayList<>());
                indexMap.put(tableName, new ArrayList<>());
            }
            List<T> list = map.get(tableName);
            list.add(model);
            List<Integer> indexList = indexMap.get(tableName);
            indexList.add(i);
        }

        int[] result = new int[models.size()];
        for (Map.Entry<String, List<T>> entry : map.entrySet()) {
            int[] ints = WriteSqlExecutorUtils.batchInsert(entry.getKey(), this.tableSchema, this.databaseType, executeBatchUpdate, entry.getValue(), exceptProps);
            List<Integer> indexList = indexMap.get(entry.getKey());
            for (int i = 0; i < ints.length; i++) {
                result[indexList.get(i)] = ints[i];
            }
        }

        return result;
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
