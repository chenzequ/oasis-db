package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.aggregate.AggregateOperator;
import cn.oasissoft.core.db.entity.schema.ShardingKeys;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QuerySingleResultFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 17:22
 */
public class QuerySingleShardingSqlExecutor<T, K> extends ShardingSqlExecutorBase<T, K> {

    private final QuerySingleResultFunction querySingleResult;

    public QuerySingleShardingSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QuerySingleResultFunction querySingleResult) {
        super(tableSchema, databaseType);
        Assert.notNull(querySingleResult, "querySingleResult is null.");
        this.querySingleResult = querySingleResult;
    }

    // 公有方法

    /**
     * 查询单个结果
     *
     * @param query
     * @param op
     * @param propertyOrSql
     * @return
     */
    public <V> Object singleResultBy(ShardingKeys<V> ShardingKeys, DbQuery query, AggregateOperator op, String propertyOrSql) {
        String tableName = this.tableNameSqlByShardingKeys(ShardingKeys);
        return QuerySqlExecutorUtils.querySingleResult(tableName, this.tableSchema, this.databaseType, this.querySingleResult, query, op, propertyOrSql);
    }

    /**
     * 统计数量
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Long count(ShardingKeys<V> ShardingKeys,DbQuery query, String propertySql) {
        return (Long) this.singleResultBy(ShardingKeys,query, AggregateOperator.Count, propertySql);
    }

    public <V> Long count(ShardingKeys<V> ShardingKeys, DbQuery query) {
        return this.count(ShardingKeys,query, null);
    }

    /**
     * 获取单值
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Object single(ShardingKeys<V> ShardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(ShardingKeys,query, AggregateOperator.Single, propertySql);
    }

    /**
     * 统计平均值
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Object avg(ShardingKeys<V> ShardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(ShardingKeys,query, AggregateOperator.Avg, propertySql);
    }

    /**
     * 合计数量
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Object sum(ShardingKeys<V> ShardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(ShardingKeys,query, AggregateOperator.Sum, propertySql);
    }

    public <V> Object max(ShardingKeys<V> ShardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(ShardingKeys,query, AggregateOperator.Max, propertySql);
    }

    public <V> Object min(ShardingKeys<V> ShardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(ShardingKeys,query, AggregateOperator.Min, propertySql);
    }
}
