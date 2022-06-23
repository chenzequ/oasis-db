package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.schema.ShardingKey;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
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
public class QueryItemShardingSqlExecutor<T, K> extends ShardingSqlExecutorBase<T, K> {

    private final QueryForMapFunction queryForMap;

    public QueryItemShardingSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QueryForMapFunction queryForMap) {
        super(tableSchema, databaseType);
        Assert.notNull(queryForMap, "queryForMap not null.");
        this.queryForMap = queryForMap;
    }

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
        String tableName = this.tableNameSqlById(id);
        return QuerySqlExecutorUtils.queryModel(tableName, this.tableSchema, this.databaseType, this.queryForMap, id, forUpdate);
    }

    /**
     * 根据 查询条件 获取 实体 对象
     *
     * @param query 查询对象
     * @return
     */
    public T toModel(ShardingKey key, DbQuery query) {
        return this.toModel(key, query, false);
    }

    /**
     * 根据 查询条件 获取 实体 对象
     *
     * @param query     查询对象
     * @param forUpdate 是否锁行
     * @return
     */
    public T toModel(ShardingKey key, DbQuery query, boolean forUpdate) {
        String tableName = this.tableNameSqlByShardingKey(key);
        return QuerySqlExecutorUtils.queryModel(tableName, this.tableSchema, this.databaseType, this.queryForMap, query, forUpdate);
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
        return this.toView(vClass, id, false);
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
    public <V> V toView(Class<V> vClass, K id, boolean forUpdate) {
        String tableName = this.tableNameSqlById(id);
        return QuerySqlExecutorUtils.queryView(vClass, tableName, this.tableSchema, this.databaseType, this.queryForMap, id, forUpdate);
    }

    /**
     * 根据 查询条件 获取 视图 对象
     *
     * @param vClass 视图类型
     * @param query  查询对象
     * @param <V>
     * @return
     */
    public <V> V toView(Class<V> vClass, ShardingKey shardingKey, DbQuery query) {
        return this.toView(vClass, shardingKey, query, false);
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
    public <V> V toView(Class<V> vClass, ShardingKey shardingKey, DbQuery query, boolean forUpdate) {
        String tableName = this.tableNameSqlByShardingKey(shardingKey);
        return QuerySqlExecutorUtils.queryView(vClass, tableName, this.tableSchema, this.databaseType, this.queryForMap, query, forUpdate);
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
        String tableName = this.tableNameSqlById(id);
        return QuerySqlExecutorUtils.queryMap(props, tableName, this.tableSchema, this.databaseType, this.queryForMap, id, forUpdate);
    }

    /**
     * 根据 Query 获取 MapEntity 对象
     *
     * @param props 要返回的实体属性
     * @param query 查询条件
     * @return
     */
    public MapEntity toMap(Set<String> props, ShardingKey shardingKey, DbQuery query) {
        return this.toMap(props, shardingKey, query, false);
    }

    /**
     * 根据 Query 获取 MapEntity 对象
     *
     * @param props     要返回的实体属性
     * @param query     查询条件
     * @param forUpdate 是否锁行
     * @return
     */
    public MapEntity toMap(Set<String> props, ShardingKey shardingKey, DbQuery query, boolean forUpdate) {
        String tableName = this.tableNameSqlByShardingKey(shardingKey);
        return QuerySqlExecutorUtils.queryMap(props, tableName, this.tableSchema, this.databaseType, this.queryForMap, query, forUpdate);
    }

}
