package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QueryForMapFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * 获取单个模型的sql执行器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/20 00:23
 */
public class QueryItemSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final QueryForMapFunction queryForMap;

    public QueryItemSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QueryForMapFunction queryForMap) {
        super(tableSchema, databaseType);
        Assert.notNull(queryForMap, "queryForMap not null.");
        this.queryForMap = queryForMap;
    }

    // 内部方法
    // 根据query查询对象方法
//    private Map<String, Object> queryItemByQueryBase(Set<String> props, DbQuery query, boolean forUpdate) {
//        Map<String, Object> params = new HashMap<>();
//        String whereSql = DbQueryUtils.getWhereSql(this.databaseType, this.tableSchema, query, params);
//        String orderSql = DbQueryUtils.getOrderSql(this.tableSchema, query);
//        StringBuilder sb = new StringBuilder(128);
//        boolean supportTop = DatabaseType.supportTop(this.databaseType);
//        sb.append("SELECT ");
//        if (supportTop) {
//            sb.append("TOP 1 ");
//        }
//        sb.append(this.tableSchema.allColumnsSql(props)).append(" FROM").append(tableNameByQuery(query));
//
//        if (StringUtils.hasText(whereSql)) {
//            sb.append(" WHERE ").append(whereSql);
//        }
//
//        if (StringUtils.hasText(orderSql)) {
//            sb.append(" ORDER BY ").append(orderSql);
//        }
//
//        if (!supportTop) {
//            sb.append(" LIMIT 1");
//        }
//
//        if (forUpdate) {
//            sb.append(" FOR UPDATE");
//        }
//
//        return this.queryForMap.apply(sb.toString(), params);
//    }
//    // 根据id查询对象方法
//    private Map<String, Object> queryItemByIdBase(Set<String> props, K id, boolean forUpdate) {
//        Assert.notNull(id, "id is null.");
//        // 获取主键参数
//        Map<String, Object> params = this.tableSchema.getParamArrayById(id);
//        // 组装sql
//        boolean supportTop = DatabaseType.supportTop(this.databaseType);
//        StringBuilder sb = new StringBuilder(128);
//        sb.append("SELECT ");
//        if (supportTop) {
//            sb.append("TOP 1 ");
//        }
//        sb.append(this.tableSchema.allColumnsSql(props)).append(" FROM ").append(tableNameById(id)).append(this.tableSchema.getIdsSql(this.databaseType));
//        if (!supportTop) {
//            sb.append(" LIMIT 1");
//        }
//        if (forUpdate) {
//            sb.append(" FOR UPDATE");
//        }
//        // 执行
//        return this.queryForMap.apply(sb.toString(), params);
//    }

    // 查询模型

    /**
     * 根据 id 获取 实体 对象
     *
     * @param id 主键值
     * @return
     */
    public T toModel(K id) {
        return this.toModel(id, false);
    }

    /**
     * 根据 id 获取 实体 对象
     *
     * @param id        主键值
     * @param forUpdate 是否锁行
     * @return
     */
    public T toModel(K id, boolean forUpdate) {
        return QuerySqlExecutorUtils.queryModel(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForMap, id, forUpdate);
    }

    /**
     * 根据 查询条件 获取 实体 对象
     *
     * @param query 查询对象
     * @return
     */
    public T toModel(DbQuery query) {
        return this.toModel(query, false);
    }

    /**
     * 根据 查询条件 获取 实体 对象
     *
     * @param query     查询对象
     * @param forUpdate 是否锁行
     * @return
     */
    public T toModel(DbQuery query, boolean forUpdate) {
        return QuerySqlExecutorUtils.queryModel(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForMap, query, forUpdate);
    }

    // 查询视图对象

    /**
     * 根据 id 获取 视图 对象
     *
     * @param vClass 视图类型
     * @param id     主键值
     * @param <V>
     * @return
     */
    public <V> V toView(Class<V> vClass, K id) {
        return this.toView(vClass, null, id);
    }

    public <V> V toView(Class<V> vClass, Set<String> exceptProps, K id) {
        return this.toView(vClass, exceptProps, id, false);
    }

    public <V> V toView(Class<V> vClass, K id, boolean forUpdate) {
        return this.toView(vClass, null, id, forUpdate);
    }

    /**
     * 根据 id 获取 视图 对象
     *
     * @param vClass    视图类型
     * @param id        主键值
     * @param forUpdate 是否锁行
     * @param <V>
     * @return
     */
    public <V> V toView(Class<V> vClass, Set<String> exceptProps, K id, boolean forUpdate) {
        return QuerySqlExecutorUtils.queryView(vClass, exceptProps, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForMap, id, forUpdate);
    }

    /**
     * 根据 查询条件 获取 视图 对象
     *
     * @param vClass 视图类型
     * @param query  查询对象
     * @param <V>
     * @return
     */
    public <V> V toView(Class<V> vClass, DbQuery query) {
        return this.toView(vClass, null, query);
    }

    public <V> V toView(Class<V> vClass, Set<String> exceptProps, DbQuery query) {
        return this.toView(vClass, exceptProps, query, false);
    }

    /**
     * 根据 查询条件 获取 视图 对象
     *
     * @param vClass    视图类型
     * @param query     查询对象
     * @param forUpdate 是否锁行
     * @param <V>
     * @return
     */
    public <V> V toView(Class<V> vClass, DbQuery query, boolean forUpdate) {
        return this.toView(vClass, null, query, forUpdate);
    }

    public <V> V toView(Class<V> vClass, Set<String> exceptProps, DbQuery query, boolean forUpdate) {
        return QuerySqlExecutorUtils.queryView(vClass, exceptProps, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForMap, query, forUpdate);
    }

    // 查询Map对象

    /**
     * 根据 id 获取 MapEntity 对象
     *
     * @param props 要返回的实体属性
     * @param id    主键值
     * @return
     */
    public MapEntity toMap(Set<String> props, K id) {
        return this.toMap(props, id, false);
    }

    /**
     * 根据 id 获取 MapEntity 对象
     *
     * @param props     要返回的实体属性
     * @param id        主键值
     * @param forUpdate 是否锁行
     * @return
     */
    public MapEntity toMap(Set<String> props, K id, boolean forUpdate) {
        return QuerySqlExecutorUtils.queryMap(props, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForMap, id, forUpdate);
    }

    /**
     * 根据 Query 获取 MapEntity 对象
     *
     * @param props 要返回的实体属性
     * @param query 查询条件
     * @return
     */
    public MapEntity toMap(Set<String> props, DbQuery query) {
        return this.toMap(props, query, false);
    }

    /**
     * 根据 Query 获取 MapEntity 对象
     *
     * @param props     要返回的实体属性
     * @param query     查询条件
     * @param forUpdate 是否锁行
     * @return
     */
    public MapEntity toMap(Set<String> props, DbQuery query, boolean forUpdate) {
        return QuerySqlExecutorUtils.queryMap(props, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForMap, query, forUpdate);
    }

}
