package cn.oasissoft.core.db.executor.write;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.UpdateObject;
import cn.oasissoft.core.db.entity.UpdateSqlObject;
import cn.oasissoft.core.db.entity.pk.UnionPK;
import cn.oasissoft.core.db.entity.schema.ColumnSchema;
import cn.oasissoft.core.db.entity.schema.PrimaryKeyStrategy;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.executor.function.ExecuteBatchUpdateFunction;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateAutoIncrementFunction;
import cn.oasissoft.core.db.executor.function.ExecuteUpdateFunction;
import cn.oasissoft.core.db.query.DbCriterionOperator;
import cn.oasissoft.core.db.query.DbLoginOperator;
import cn.oasissoft.core.db.query.DbOperandOperator;
import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.query.DbSubQuery;
import cn.oasissoft.core.db.utils.DbQueryUtils;
import cn.oasissoft.core.db.utils.SnowIdUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 写入sql执行器辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/21 11:56
 */
final class WriteSqlExecutorUtils {

    private static <T, K> String tableNameById(TableSchema<T> schema, K id) {
        String tableName = schema.getShardingTable().getTableNameById(schema.getTableName(), id);
        return schema.getTableNameSql(tableName);
    }

    private static <T> String tableNameByModel(TableSchema<T> schema, T model) {
        String tableName = schema.getShardingTable().getTableNameByModel(schema.getTableName(), model);
        return schema.getTableNameSql(tableName);
    }

    private static <T, K> String tableNameByQuery(TableSchema<T> schema, K id) {
        String tableName = schema.getShardingTable().getTableNameById(schema.getTableName(), id);
        return schema.getTableNameSql(tableName);
    }

    // 获取数据库命名参数符号
    private static String getDbSymbol(DatabaseType databaseType) {
        return DatabaseType.getSymbol(databaseType);
    }

    private static <T, K> DbQuery getPrimaryKeysQuery(TableSchema<T> schema, List<K> ids) {
        DbQuery query = new DbQuery();
        if (CollectionUtils.isEmpty(ids)) {
            query.setReturnData(false);
        } else {
            if (schema.getPrimaryKeys().length == 1) {
                // 单主键
                String property = schema.getFirstPrimaryKey().getProperty();
                if (ids.size() == 1) {
                    // 一个值(equal)
                    query.addCriterion(property, ids.get(0));
                } else {
                    // 从个值(in)
                    query.addCriterion(property, ids, DbCriterionOperator.In);
                }
            } else {
                // 多主键 (id1=xx AND id2=xx) or (...)
                for (K id : ids) {
                    UnionPK unionPK = (UnionPK) id;
                    Object[] idValues = unionPK.getIdValues();
                    DbSubQuery subQuery = new DbSubQuery(DbLoginOperator.Or);
                    ColumnSchema[] primaryKeys = schema.getPrimaryKeys();
                    for (int i = 0; i < primaryKeys.length; i++) {
                        subQuery.add(primaryKeys[i].getProperty(), idValues[i]);
                    }
                }
            }
        }
        return query;
    }

    private static void checkUpdatePrimaryKey(TableSchema schema, String prop) {
        if (schema.isPrimaryKey(prop)) {
            throw new OasisDbDefineException(String.format("不允许更新主键属性[%s]", prop));
        }
    }

    // 检查是否是属性
    private static ColumnSchema getColumnBy(TableSchema schema, String property) {
        Assert.hasText(property, "property is null");
        ColumnSchema column = schema.getColumnByProperty(property);
        if (column == null) {
            throw new OasisDbDefineException(String.format("未知属性[%s]", property));
        }
        return column;
    }

    private static void checkUpdateObjects(TableSchema schema, UpdateObject[] updateObjects) {
        Assert.notEmpty(updateObjects, "updateObjects is empty");
        Set<String> props = new HashSet<>(updateObjects.length);
        for (UpdateObject updateObject : updateObjects) {
            checkUpdatePrimaryKey(schema, updateObject.getProp());
            if (props.contains(updateObject.getProp())) {
                throw new OasisDbDefineException("更新中存在相同的属性[" + updateObject.getProp() + "]");
            }
            props.add(updateObject.getProp());
        }
        props.clear();
    }

    private static void checkUpdateObjects(TableSchema schema, UpdateSqlObject[] updateObjects) {
        Assert.notEmpty(updateObjects, "updateObjects is empty");
        Set<String> props = new HashSet<>(updateObjects.length);
        for (UpdateSqlObject updateObject : updateObjects) {
            checkUpdatePrimaryKey(schema, updateObject.getProp());
            if (props.contains(updateObject.getProp())) {
                throw new OasisDbDefineException("更新中存在相同的属性[" + updateObject.getProp() + "]");
            }
            props.add(updateObject.getProp());
        }
        props.clear();
    }

    // 插入
    public static <T> int insert(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateAutoIncrementFunction executeUpdateFunction, T model, Set<String> exceptProps) {
        Assert.notNull(model, "model is null.");
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder(64).append("INSERT INTO ").append(tableNameSql).append("(");
        StringBuilder sb2 = new StringBuilder(32);

        boolean onePrimaryKeys = schema.getPrimaryKeys().length == 1;
        boolean autoIncrement = schema.isAutoIncrement();

        String dbSymbol = getDbSymbol(dbType);
        for (ColumnSchema column : schema.getColumns()) {
            // 除外属性跳过
            if (exceptProps != null && exceptProps.contains(column.getProperty())) {
                // 如果存在除外属性中，则不插入
                continue;
            }

            boolean isPk = schema.isPrimaryKey(column.getProperty());

            if (onePrimaryKeys && autoIncrement && isPk) {
                // 自增主键，则不插入
                continue;
            }
            if (isPk) {
                if (schema.getPrimaryKeyStrategy().equals(PrimaryKeyStrategy.SnowId)) {
                    column.setValue(model, SnowIdUtils.nextId());
                } else if (schema.getPrimaryKeyStrategy().equals(PrimaryKeyStrategy.UUID)) {
                    column.setValue(model, UUID.randomUUID().toString().replace("-", ""));
                }
            }
            sb.append(column.getColumnNameSql()).append(",");
            sb2.append(dbSymbol).append(column.getColumnName()).append(",");
            parameters.put(column.getColumnName(), column.getValue(model));      //添加参数

        }
        sb.deleteCharAt(sb.length() - 1);
        sb2.deleteCharAt(sb2.length() - 1);
        sb.append(") VALUES(").append(sb2).append(")");
        //执行Sql
        int result = executeUpdateFunction.apply(sb.toString(), parameters, autoIncrement);
        if (schema.isAutoIncrement()) {
            if (result > 0) {
                schema.getFirstPrimaryKey().setValue(model, result);
            }
        }
        return result;
    }

    // 插入
    public static <T> int insert(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateAutoIncrementFunction executeUpdateFunction, T model) {
        return insert(tableNameSql, schema, dbType, executeUpdateFunction, model, null);
    }

//    // 插入(单表)
//    public static <T> int insert(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateAutoIncrementFunction executeUpdateFunction, T model, Set<String> exceptProps) {
//        return insert(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, model, exceptProps);
//    }

//    // 插入(单表)
//    public static <T> int insert(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateAutoIncrementFunction executeUpdateFunction, T model) {
//        return insert(schema, dbType, executeUpdateFunction, model, null);
//    }

    // 批量插入
    public static <T> int[] batchInsert(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteBatchUpdateFunction executeBatchUpdate, List<T> models, Set<String> exceptProps) {
        Assert.notNull(models, "models is null.");
        if (models.size() == 0) {
            return new int[0];
        }

        Map<String, Object>[] parameters = new Map[models.size()];

        StringBuilder sb = new StringBuilder(64).append("INSERT INTO ").append(tableNameSql).append("(");
        StringBuilder sb2 = new StringBuilder(32);

        boolean onePrimaryKeys = schema.getPrimaryKeys().length == 1;
        boolean autoIncrement = onePrimaryKeys && schema.getPrimaryKeyStrategy().equals(PrimaryKeyStrategy.AutoIncrement);

        String dbSymbol = getDbSymbol(dbType);
        for (ColumnSchema column : schema.getColumns()) {
            // 除外属性跳过
            if (exceptProps != null && exceptProps.contains(column.getProperty())) {
                // 如果存在除外属性中，则不插入
                continue;
            }

            boolean isPk = schema.isPrimaryKey(column.getProperty());

            if (onePrimaryKeys && autoIncrement && isPk) {
                // 自增主键，则不插入
                continue;
            }

            sb.append(column.getColumnNameSql()).append(",");
            sb2.append(dbSymbol).append(column.getColumnName()).append(",");
            //添加参数
            for (int i = 0; i < models.size(); i++) {
                if (parameters[i] == null) {
                    parameters[i] = new HashMap<>();
                }
                if (isPk) {
                    if (schema.getPrimaryKeyStrategy().equals(PrimaryKeyStrategy.SnowId)) {
                        column.setValue(models.get(i), SnowIdUtils.nextId());
                    } else if (schema.getPrimaryKeyStrategy().equals(PrimaryKeyStrategy.UUID)) {
                        column.setValue(models.get(i), UUID.randomUUID().toString().replace("-", ""));
                    }
                }
                parameters[i].put(column.getColumnName(), column.getValue(models.get(i)));
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb2.deleteCharAt(sb2.length() - 1);
        sb.append(") VALUES(").append(sb2).append(")");
        String sql = sb.toString();

        return executeBatchUpdate.apply(sql, parameters);
    }

//    // 批量插入(单表)
//    public static <T> int[] batchInsert(TableSchema<T> schema, DatabaseType dbType, ExecuteBatchUpdateFunction executeBatchUpdate, List<T> models, Set<String> exceptProps) {
//        return batchInsert(schema.getTableNameSql(), schema, dbType, executeBatchUpdate, models, exceptProps);
//    }

    // 批量插入
    public static <T> int[] batchInsert(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteBatchUpdateFunction executeBatchUpdate, List<T> models) {
        return batchInsert(tableNameSql, schema, dbType, executeBatchUpdate, models, null);
    }

//    // 批量插入(单表)
//    public static <T> int[] batchInsert(TableSchema<T> schema, DatabaseType dbType, ExecuteBatchUpdateFunction executeBatchUpdate, List<T> models) {
//        return batchInsert(schema.getTableNameSql(), schema, dbType, executeBatchUpdate, models);
//    }

    // 更新
    public static <T> int update(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, T model, Set<String> exceptProps) {
        Assert.notNull(model, "model is null.");

        Map<String, Object> parameters = new HashMap<>();

        //生成 SQL: UPDATE [表] SET
        StringBuilder sb = new StringBuilder(64);
        sb.append("UPDATE ").append(tableNameSql).append(" SET ");

        //生成 SQL: `xxx`=:xxx,... 并且填充参数
        String dbSymbol = getDbSymbol(dbType);
        for (ColumnSchema column : schema.getColumns()) {
            if (exceptProps != null && exceptProps.contains(column.getProperty())) {
                // 如果配置在除非属性里面，则不进行更新
                continue;
            } else {
                // 主键不能更新
                if (!schema.isPrimaryKey(column)) {
                    parameters.put(column.getColumnName(), column.getValue(model));
                    sb.append(column.getColumnNameSql()).append("=").append(dbSymbol).append(column.getColumnName()).append(",");
                }
            }
        }
        //生成 SQL: WHERE `KEY1`=:KEY1 [AND `KEY2`=:KEY2]
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" WHERE ");

        for (int i = 0; i < schema.getPrimaryKeys().length; i++) {
            if (i != 0) {
                sb.append(" AND ");
            }
            ColumnSchema primaryKey = schema.getPrimaryKeys()[i];
            sb.append(primaryKey.getColumnNameSql()).append("=").append(dbSymbol).append(primaryKey.getColumnName());
            parameters.put(primaryKey.getColumnName(), primaryKey.getValue(model));
        }

        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

    // 更新
    public static <T> int update(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, T model) {
        return update(tableNameSql, schema, dbType, executeUpdateFunction, model, null);
    }

    // 更新(单表)
//    public static <T> int update(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, T model, Set<String> exceptProps) {
//        return update(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, model, exceptProps);
//    }

//    // 更新(单表)
//    public static <T> int update(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, T model) {
//        return update(schema, dbType, executeUpdateFunction, model, null);
//    }

    // 批量更新
    public static <T> int updates(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, UpdateObject<T>[] updateObjects) {
        checkUpdateObjects(schema, updateObjects);
        if (query != null && query.isNotReturnData()) {
            return 0;
        }

        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sb = new StringBuilder(32);
        sb.append("UPDATE ").append(tableNameSql).append(" SET ");
        String dbSymbol = getDbSymbol(dbType);
        for (UpdateObject<T> uo : updateObjects) {
            if (null == uo) {
                continue;
            }
            ColumnSchema column = schema.getColumnByProperty(uo.getProp());
            sb.append(column.getColumnNameSql()).append("=").append(dbSymbol).append(column.getColumnName()).append(",");
            parameters.put(column.getColumnName(), uo.getValue());
        }

        sb.deleteCharAt(sb.length() - 1);    //去掉,号

        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, parameters);
        sb.append(whereSql.length() > 0 ? " WHERE " + whereSql : "");

        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    // 批量更新(单表)
//    public static <T> int updates(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, UpdateObject<T>[] updateObjects) {
//        return updates(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, query, updateObjects);
//    }

    // 批量更新
    public static <T, K> int updates(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, UpdateObject<T>[] updateObjects) {
        DbQuery query = getPrimaryKeysQuery(schema, ids);
        return updates(tableNameSql, schema, dbType, executeUpdateFunction, query, updateObjects);
    }

//    // 批量更新(单表)
//    public static <T, K> int updates(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, UpdateObject<T>[] updateObjects) {
//        return updates(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, ids, updateObjects);
//    }

    // 批量更新sql
    public static <T> int updates(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, UpdateSqlObject<T>[] updateSqlObjects) {
        checkUpdateObjects(schema, updateSqlObjects);
        if (query != null && query.isNotReturnData()) {
            return 0;
        }

        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sb = new StringBuilder(32);
        sb.append("UPDATE ").append(tableNameSql).append(" SET ");
        String dbSymbol = getDbSymbol(dbType);
        for (UpdateSqlObject<T> uo : updateSqlObjects) {
            if (null == uo) {
                continue;
            }
            ColumnSchema column = schema.getColumnByProperty(uo.getProp());
            sb.append(column.getColumnNameSql()).append("=").append(uo.getSql()).append(",");
            if (uo.getParams() != null && uo.getParams().size() > 0) {
                for (Map.Entry<String, Object> entry : uo.getParams().entrySet()) {
                    parameters.put(entry.getKey(), entry.getValue());
                }
            }
        }

        sb.deleteCharAt(sb.length() - 1);    //去掉,号

        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, parameters);
        sb.append(whereSql.length() > 0 ? " WHERE " + whereSql : "");

        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    // 批量更新sql(单表)
//    public static <T> int updates(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, UpdateSqlObject<T>[] updateSqlObjects) {
//        return updates(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, query, updateSqlObjects);
//    }

    // 批量更新sql
    public static <T, K> int updates(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, UpdateSqlObject<T>[] updateSqlObjects) {
        DbQuery query = getPrimaryKeysQuery(schema, ids);
        return updates(tableNameSql, schema, dbType, executeUpdateFunction, query, updateSqlObjects);
    }

//    // 批量更新sql(单表)
//    public static <T, K> int updates(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, UpdateSqlObject<T>[] updateSqlObjects) {
//        return updates(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, ids, updateSqlObjects);
//    }

    // 批量取反更新
    public static <T, K> int updatesValueFlip(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, String property) {
        ColumnSchema column = getColumnBy(schema, property);
        if (column.getType() != Boolean.class) {
            throw new OasisDbDefineException("进行取反运算的属性必须是布尔类型!属性[" + property + "]不是布尔类型");
        }

        checkUpdatePrimaryKey(schema, property);
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder(32);
        sb.append("UPDATE `").append(tableNameSql).append("` SET ");

        sb.append(column.getColumnNameSql()).append("=(").append(column.getColumnNameSql()).append("+1)%2");
        // mysql不支持 会出现 【BIGINT UNSIGNED value is out of range】依版本MySql支持
        //sb.append(column.getColumnName()).append("=ABS(").append(column.getColumnName()).append("-1)");

        //使用位运算符取反h2数据库不支持
//        sb.append(column.getColumnName())
//                .append("=")
//                .append(column.getColumnName())
//                .append(" ^ 1");

        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, parameters);
        if (whereSql.length() > 0) {
            sb.append(" WHERE ").append(whereSql);
        }
        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    // 批量取反更新(单表)
//    public static <T, K> int updatesValueFlip(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, String property) {
//        return updatesValueFlip(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, query, property);
//    }

    // 批量取反更新
    public static <T, K> int updatesValueFlip(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, String property) {
        return updatesValueFlip(tableNameSql, schema, dbType, executeUpdateFunction, getPrimaryKeysQuery(schema, ids), property);
    }

//    // 批量取反更新(单表)
//    public static <T, K> int updatesValueFlip(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, String property) {
//        return updatesValueFlip(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, ids, property);
//    }

    // 批量运算更新
    public static <T, K> int updatesNumber(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
        Assert.notNull(value, "value is null.");
        ColumnSchema column = getColumnBy(schema, property);
        checkUpdatePrimaryKey(schema, property);

        if (query != null && query.isNotReturnData()) {
            return 0;
        }

        String dbSymbol = getDbSymbol(dbType);
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sb = new StringBuilder(32);
        sb.append("UPDATE ").append(tableNameSql).append(" SET ");

        // 处理 Number
        if (dbType.equals(DatabaseType.MySql) && column.isDateTimeType()) {
            //如果数据库为MySql并且类型为时间类型
            if (op == DbOperandOperator.Divide || op == DbOperandOperator.Multiply) {
                throw new OasisDbDefineException("时间格式不允许使用乘除运算操作");
            }
            int intervalValue = (int) value;
            if (op == DbOperandOperator.Subtract) {   //相减时，把值改成负数
                intervalValue = -intervalValue;
            }

            sb.append(column.getColumnNameSql()).append("=DATE_ADD(").append(column.getColumnNameSql()).append(",interval ").append(intervalValue).append(" second)");
        } else {
            sb.append(column.getColumnNameSql()).append("=").append(column.getColumnNameSql());
            switch (op) {
                case Add:
                    sb.append("+");
                    break;
                case Divide:
                    sb.append("/");
                    break;
                case Multiply:
                    sb.append("*");
                    break;
                case Subtract:
                    sb.append("-");
                    break;
            }
            if (value.doubleValue() < 0) {
                sb.append("(").append(value).append(")");
            } else {
                sb.append(value);
            }
        }

        // 处理 UpdateObject[]
        if (updateObjects != null && updateObjects.length > 0) {
            if (Arrays.stream(updateObjects).anyMatch(uo -> uo.getProp().equals(property))) {
                throw new OasisDbDefineException(String.format("属性[%s]在更新与运算中重复", property));
            }
            checkUpdateObjects(schema, updateObjects);
            for (UpdateObject uo : updateObjects) {
                if (uo == null) {
                    continue;
                }
                ColumnSchema col = schema.getColumnByProperty(uo.getProp());
                sb.append(",").append(col.getColumnNameSql()).append("=").append(dbSymbol).append(col.getColumnName());

                parameters.put(col.getColumnName(), uo.getValue());
            }
        }

        // 处理 Query
        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, parameters);

        if (whereSql.length() > 0) {
            sb.append(" WHERE ").append(whereSql);
        }
        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    // 批量运算更新(单表)
//    public static <T, K> int updatesNumber(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
//        return updatesNumber(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, query, property, op, value, updateObjects);
//    }

    // 批量运算更新
    public static <T, K> int updatesNumber(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
        DbQuery query = getPrimaryKeysQuery(schema, ids);
        return updatesNumber(tableNameSql, schema, dbType, executeUpdateFunction, query, property, op, value, updateObjects);
    }

//    // 批量运算更新(单表)
//    public static <T, K> int updatesNumber(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, String property, DbOperandOperator op, Number value, UpdateObject<T>[] updateObjects) {
//        return updatesNumber(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, ids, property, op, value, updateObjects);
//    }

    // 批量值替换
    public static <T, K> int updatesReplace(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, String property, String oldString, String newString) {
        ColumnSchema column = getColumnBy(schema, property);
        checkUpdatePrimaryKey(schema, property);

        if (query != null && query.isNotReturnData()) {
            return 0;
        }

        String dbSymbol = getDbSymbol(dbType);
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder(32);
        sb.append("UPDATE ").append(tableNameSql).append(" SET ");

        sb.append(column.getColumnNameSql()).append("= REPLACE(").append(column.getColumnNameSql()).append(",").append(dbSymbol).append("OLD_STRING_SIGN,").append(dbSymbol).append("NEW_STRING_SIGN)");

        parameters.put("OLD_STRING_SIGN", oldString);
        parameters.put("NEW_STRING_SIGN", newString);

        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, parameters);
        if (whereSql.length() > 0) {
            sb.append(" WHERE ").append(whereSql);
        }
        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    // 批量值替换(单表)
//    public static <T, K> int updatesReplace(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query, String property, String oldString, String newString) {
//        return updatesReplace(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, query, property, oldString, newString);
//    }

    // 批量值替换
    public static <T, K> int updatesReplace(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, String property, String oldString, String newString) {
        DbQuery query = getPrimaryKeysQuery(schema, ids);
        return updatesReplace(tableNameSql, schema, dbType, executeUpdateFunction, query, property, oldString, newString);
    }

//    // 批量值替换(单表)
//    public static <T, K> int updatesReplace(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, List<K> ids, String property, String oldString, String newString) {
//        return updatesReplace(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, ids, property, oldString, newString);
//    }

    // 删除
    public static <T, K> int delete(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, K id) {
        Assert.notNull(id, "id is null");
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder(32).append("DELETE FROM ").append(tableNameSql).append(" WHERE ");
        String dbSymbol = getDbSymbol(dbType);
        if (id instanceof UnionPK) {
            UnionPK unionPK = (UnionPK) id;
            Object[] ids = unionPK.getIdValues();
            for (int i = 0; i < schema.getPrimaryKeys().length; i++) {
                if (i != 0) {
                    sb.append(" AND ");
                }
                sb.append(schema.getPrimaryKeys()[i].getColumnNameSql()).append("=").append(dbSymbol).append(schema.getPrimaryKeys()[i].getColumnName());
                parameters.put(schema.getPrimaryKeys()[i].getColumnName(), ids[i]);
            }
        } else {
            sb.append(schema.getFirstPrimaryKey().getColumnNameSql()).append("=").append(dbSymbol).append(schema.getFirstPrimaryKey().getColumnName());
            parameters.put(schema.getFirstPrimaryKey().getColumnName(), id);
        }
        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    // 删除(单表)
//    public static <T, K> int delete(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, K id) {
//        return delete(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, id);
//    }

    public static <T, K> int delete(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query) {
        if (query != null && query.isNotReturnData()) {
            return 0;
        }

        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sb = new StringBuilder(32);
        sb.append("DELETE FROM ").append(tableNameSql);

        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, parameters);
        if (StringUtils.hasText(whereSql)) {
            sb.append(" WHERE ").append(whereSql);
        }

        return executeUpdateFunction.apply(sb.toString(), parameters);
    }

//    public static <T, K> int delete(TableSchema<T> schema, DatabaseType dbType, ExecuteUpdateFunction executeUpdateFunction, DbQuery query) {
//        return delete(schema.getTableNameSql(), schema, dbType, executeUpdateFunction, query);
//    }
}
