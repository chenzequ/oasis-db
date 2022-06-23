package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.PageList;
import cn.oasissoft.core.db.entity.aggregate.AggregateOperator;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbException;
import cn.oasissoft.core.db.executor.function.QueryForListFunction;
import cn.oasissoft.core.db.executor.function.QueryForMapFunction;
import cn.oasissoft.core.db.executor.function.QuerySingleResultFunction;
import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.utils.DbQueryUtils;
import cn.oasissoft.core.db.utils.DbReflectUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 查询Sql执行器辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/21 23:36
 */
final class QuerySqlExecutorUtils {

    // item

    private static <T, K> Map<String, Object> queryItemById(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, Set<String> props, K id, boolean forUpdate) {
        Assert.notNull(id, "id is null.");
        // 获取主键参数
        Map<String, Object> params = schema.getParamArrayById(id);
        // 组装sql
        boolean supportTop = DatabaseType.supportTop(dbType);
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT ");
        if (supportTop) {
            sb.append("TOP 1 ");
        }
        sb.append(schema.allColumnsSql(props)).append(" FROM ").append(tableNameSql).append(" WHERE ").append(schema.getIdsSql(dbType));
        if (!supportTop) {
            sb.append(" LIMIT 1");
        }
        if (forUpdate) {
            sb.append(" FOR UPDATE");
        }
        // 执行
        return queryForMap.apply(sb.toString(), params);
    }

    private static <T, K> Map<String, Object> queryItemByQuery(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, Set<String> props, DbQuery query, boolean forUpdate) {
        Map<String, Object> params = new HashMap<>();
        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, params);
        String orderSql = DbQueryUtils.getOrderSql(schema, query);
        StringBuilder sb = new StringBuilder(128);
        boolean supportTop = DatabaseType.supportTop(dbType);
        sb.append("SELECT ");
        if (supportTop) {
            sb.append("TOP 1 ");
        }
        sb.append(schema.allColumnsSql(props)).append(" FROM").append(tableNameSql);

        if (StringUtils.hasText(whereSql)) {
            sb.append(" WHERE ").append(whereSql);
        }

        if (StringUtils.hasText(orderSql)) {
            sb.append(" ORDER BY ").append(orderSql);
        }

        if (!supportTop) {
            sb.append(" LIMIT 1");
        }

        if (forUpdate) {
            sb.append(" FOR UPDATE");
        }

        return queryForMap.apply(sb.toString(), params);
    }

    /**
     * 根据id获取对象
     *
     * @param tableNameSql 表名sql - (单表: schema.getTableNameSql())
     * @param schema       表结构
     * @param dbType       数据库类型
     * @param queryForMap  查询方法
     * @param id           主健值
     * @param forUpdate    是否锁行
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> T queryModel(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, K id, boolean forUpdate) {
        Map<String, Object> dbMap = queryItemById(tableNameSql, schema, dbType, queryForMap, null, id, forUpdate);
        return dbMap == null ? null : schema.loadModelByColumnName(dbMap);
    }

    public static <T, K> T queryModel(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, DbQuery query, boolean forUpdate) {
        Map<String, Object> dbMap = queryItemById(tableNameSql, schema, dbType, queryForMap, null, query, forUpdate);
        return dbMap == null ? null : schema.loadModelByColumnName(dbMap);
    }

    public static <T, K, V> V queryView(Class<V> vClass, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, K id, boolean forUpdate) {
        Map<String, Field> fieldMap = DbReflectUtils.getAllPrivateFieldsMap(vClass);
        Map<String, Object> dbMap = queryItemById(tableNameSql, schema, dbType, queryForMap, fieldMap.keySet(), id, forUpdate);
        return dbMap == null ? null : schema.convertDbMapToEntity(vClass, fieldMap, dbMap);
    }

    public static <T, K, V> V queryView(Class<V> vClass, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, DbQuery query, boolean forUpdate) {
        Map<String, Field> fieldMap = DbReflectUtils.getAllPrivateFieldsMap(vClass);
        Map<String, Object> dbMap = queryItemById(tableNameSql, schema, dbType, queryForMap, fieldMap.keySet(), query, forUpdate);
        return dbMap == null ? null : schema.convertDbMapToEntity(vClass, fieldMap, dbMap);
    }

    public static <T, K> MapEntity queryMap(Set<String> props, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, K id, boolean forUpdate) {
        Map<String, Object> dbMap = queryItemById(tableNameSql, schema, dbType, queryForMap, props, id, forUpdate);
        return dbMap == null ? null : schema.convertDbMapToPropertyMap(dbMap);
    }

    public static <T, K> MapEntity queryMap(Set<String> props, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForMapFunction queryForMap, DbQuery query, boolean forUpdate) {
        Map<String, Object> dbMap = queryItemById(tableNameSql, schema, dbType, queryForMap, props, query, forUpdate);
        return dbMap == null ? null : schema.convertDbMapToPropertyMap(dbMap);
    }

    // List
    private static <T, K> List<Map<String, Object>> queryList(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, Set<String> props, DbQuery query, int size, int index) {
        Map<String, Object> params = new HashMap<>();
        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, params);
        String orderSql = DbQueryUtils.getOrderSql(schema, query);
        StringBuilder sql = new StringBuilder(128);
        String where = StringUtils.hasText(whereSql) ? " WHERE " + whereSql : "";
        String order = StringUtils.hasText(orderSql) ? " ORDER BY " + orderSql : "";
        if (index == 1) {
            // 第一页
            if (DatabaseType.supportTop(dbType)) {
                sql.append("SELECT ").append(size > 0 ? "TOP " + size : "").append(schema.allColumnsSql(props)).append(" FROM ").append(tableNameSql).append(where).append(order);
            } else {
                sql.append("SELECT ").append(schema.allColumnsSql(props)).append(" FROM ").append(tableNameSql).append(where).append(order).append(size > 0 ? " LIMIT " + size : "");
            }
        } else if (index > 1) {
            // 第一页之后的页
            if (DatabaseType.supportTop(dbType)) {
                //"SELECT {返回字段} FROM (SELECT ROW_NUMBER() OVER({排序})rownumber,{返回字段} FROM {表名}{条件})a WHERE rownumber>{上一页末尾} AND rownumber<{下一页开头}"
                if (order.length() == 0) {
                    order = " ORDER BY " + schema.getFirstPrimaryKey().getColumnNameSql();
                }
                String columnsSql = schema.allColumnsSql(props);
                sql.append("SELECT ").append(columnsSql).append(" FROM (SELECT ROW_NUMBER() OVER(").append(order).append(")R_N,").append(columnsSql).append(" FROM ").append(tableNameSql).append(where).append(")T WHERE R_N>").append((index - 1) * size).append(" AND R_N<").append((index * size) + 1);
            } else {
                //MySql&SQLite
                //"SELECT {返回字段} FROM {表名}{条件}{排序} LIMIT {跳过数量},{查询数量}"
                sql.append("SELECT ").append(schema.allColumnsSql(props)).append(" FROM ").append(tableNameSql).append(where).append(order).append(" LIMIT ").append((index - 1) * size).append(",").append(size);
            }
        } else {
            throw new OasisDbException("无效页号[" + index + "]");
        }

        return queryForList.apply(sql.toString(), params);
    }

    public static <T, K> List<T> queryModels(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, DbQuery query, int size, int index) {
        List<T> result = new ArrayList<>();
        if (null != query && query.isNotReturnData()) {
            return result;
        }
        List<Map<String, Object>> dbMaps = queryList(tableNameSql, schema, dbType, queryForList, null, query, size, index);
        for (Map<String, Object> dbMap : dbMaps) {
            result.add(schema.loadModelByColumnName(dbMap));
        }
        return result;
    }

    public static <T, K> List<T> queryModels(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, DbQuery query) {
        return queryModels(tableNameSql, schema, dbType, queryForList, query, -1, 1);
    }

    public static <T, K, V> List<V> queryViews(Class<V> vClass, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, DbQuery query, int size, int index) {
        List<V> result = new ArrayList<>();
        if (null != query && query.isNotReturnData()) {
            return result;
        }
        Map<String, Field> fieldMap = DbReflectUtils.getAllPrivateFieldsMap(vClass);
        List<Map<String, Object>> dbMaps = queryList(tableNameSql, schema, dbType, queryForList, fieldMap.keySet(), query, size, index);
        for (Map<String, Object> dbMap : dbMaps) {
            result.add(schema.convertDbMapToEntity(vClass, fieldMap, dbMap));
        }
        return result;
    }

    public static <T, K, V> List<V> queryViews(Class<V> vClass, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, DbQuery query) {
        return queryViews(vClass, tableNameSql, schema, dbType, queryForList, query, -1, 1);
    }

    public static <T, K> List<MapEntity> queryMaps(Set<String> props, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, DbQuery query, int size, int index) {
        List<MapEntity> result = new ArrayList<>();
        if (null != query && query.isNotReturnData()) {
            return result;
        }
        List<Map<String, Object>> dbMaps = queryList(tableNameSql, schema, dbType, queryForList, props, query, size, index);
        for (Map<String, Object> dbMap : dbMaps) {
            result.add(schema.convertDbMapToPropertyMap(dbMap));
        }
        return result;
    }

    public static <T, K> List<MapEntity> queryMaps(Set<String> props, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, DbQuery query) {
        return queryMaps(props, tableNameSql, schema, dbType, queryForList, query, -1, 1);
    }

    // Page
    public static <T, K> PageList<T> queryPageModels(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, QuerySingleResultFunction querySingleResult, DbQuery query, int size, int index) {
        if (null != query && query.isNotReturnData()) {
            return new PageList<>(new ArrayList<>(0), 0);
        }
        long total = count(tableNameSql, schema, dbType, querySingleResult, query, null);
        if (total == 0L) {
            return new PageList<>(new ArrayList<>(0), 0);
        }
        List<T> models = queryModels(tableNameSql, schema, dbType, queryForList, query, size, index);
        return new PageList<>(models, total);
    }

    public static <T, K, V> PageList<V> queryPageViews(Class<V> vClass, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, QuerySingleResultFunction querySingleResult, DbQuery query, int size, int index) {
        if (null != query && query.isNotReturnData()) {
            return new PageList<>(new ArrayList<>(0), 0);
        }
        long total = count(tableNameSql, schema, dbType, querySingleResult, query, null);
        if (total == 0L) {
            return new PageList<>(new ArrayList<>(0), 0);
        }
        List<V> models = queryViews(vClass, tableNameSql, schema, dbType, queryForList, query, size, index);
        return new PageList<>(models, total);
    }

    public static <T, K> PageList<MapEntity> queryPageMaps(Set<String> props, String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, QuerySingleResultFunction querySingleResult, DbQuery query, int size, int index) {
        if (null != query && query.isNotReturnData()) {
            return new PageList<>(new ArrayList<>(0), 0);
        }
        long total = count(tableNameSql, schema, dbType, querySingleResult, query, null);
        if (total == 0L) {
            return new PageList<>(new ArrayList<>(0), 0);
        }
        List<MapEntity> models = queryMaps(props, tableNameSql, schema, dbType, queryForList, query, size, index);
        return new PageList<>(models, total);
    }


    // Single Result
    public static <T, K> Object querySingleResult(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QuerySingleResultFunction querySingleResult, DbQuery query, AggregateOperator op, String columnSql) {
        if (null != query && query.isNotReturnData()) {
            return op.equals(AggregateOperator.Single) ? null : 0L;
        }
        columnSql = StringUtils.hasText(columnSql) ? columnSql : "*";
        Map<String, Object> params = new HashMap<>();
        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, params);
        String opSql = AggregateOperator.getOpSql(op);

        StringBuilder sql = new StringBuilder(64);
        sql.append("SELECT ");
        // 临时变量，用于决定在Sql结尾是否添加Limit语句
        boolean endAddLimit = false;
        if (op.equals(AggregateOperator.Single)) {
            // Single运算符
            if (DatabaseType.supportTop(dbType)) {
                sql.append("TOP 1 ").append(columnSql);
            } else {
                sql.append(columnSql);
                endAddLimit = true;
            }
        } else {
            sql.append(opSql).append("(").append(columnSql).append(")");
        }

        sql.append(" FROM ").append(tableNameSql);

        if (whereSql.length() > 0) {
            sql.append(" WHERE ").append(whereSql);
        }

        if (op.equals(AggregateOperator.Single)) {
            String orderSql = DbQueryUtils.getOrderSql(schema, query);
            if (orderSql.length() > 0) {
                sql.append(" ORDER BY ").append(orderSql);
            }
        }

        if (endAddLimit) {
            sql.append(" LIMIT 1");
        }

        Object result = querySingleResult.apply(sql.toString(), params);

        if (op.equals(AggregateOperator.Sum) || op.equals(AggregateOperator.Avg) || op.equals(AggregateOperator.Count)) {
            return result == null ? 0L : Long.parseLong(result.toString());
        } else {
            return result;
        }
    }

    public static <T, K> long count(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QuerySingleResultFunction querySingleResult, DbQuery query, String columnSql) {
        return (long) querySingleResult(tableNameSql, schema, dbType, querySingleResult, query, AggregateOperator.Count, columnSql);
    }
}
