package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.aggregate.AggregateOperator;
import cn.oasissoft.core.db.entity.schema.ShardingKeys;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.ShardingSqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QuerySingleResultFunction;
import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.query.LambdaFunction;
import cn.oasissoft.core.db.utils.LambdaUtils;
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
    public <V> Object singleResultBy(ShardingKeys<V> shardingKeys, DbQuery query, AggregateOperator op, String propertyOrSql) {
        String tableName = this.tableNameSqlByShardingKeys(shardingKeys);
        return QuerySqlExecutorUtils.querySingleResult(tableName, this.tableSchema, this.databaseType, this.querySingleResult, query, op, propertyOrSql);
    }

    public <V> Object singleResultBy(ShardingKeys<V> shardingKeys, DbQuery query, AggregateOperator op, LambdaFunction<T> lambdaFunction) {
        return this.singleResultBy(shardingKeys, query, op, LambdaUtils.getPropertyName(lambdaFunction));
    }

    /**
     * 统计数量
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Long count(ShardingKeys<V> shardingKeys, DbQuery query, String propertySql) {
        return (Long) this.singleResultBy(shardingKeys, query, AggregateOperator.Count, propertySql);
    }

    public <V> Long count(ShardingKeys<V> shardingKeys, DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.count(shardingKeys, query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    public <V> Long count(ShardingKeys<V> shardingKeys, DbQuery query) {
        return this.count(shardingKeys, query, (String) null);
    }

    /**
     * 获取单值
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Object single(ShardingKeys<V> shardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(shardingKeys, query, AggregateOperator.Single, propertySql);
    }

    public <V> Object single(ShardingKeys<V> shardingKeys, DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.single(shardingKeys, query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    /**
     * 统计平均值
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Object avg(ShardingKeys<V> shardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(shardingKeys, query, AggregateOperator.Avg, propertySql);
    }

    public <V> Object avg(ShardingKeys<V> shardingKeys, DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.avg(shardingKeys, query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    /**
     * 合计数量
     *
     * @param query
     * @param propertySql
     * @return
     */
    public <V> Object sum(ShardingKeys<V> shardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(shardingKeys, query, AggregateOperator.Sum, propertySql);
    }

    public <V> Object sum(ShardingKeys<V> shardingKeys, DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.sum(shardingKeys, query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    public <V> Object max(ShardingKeys<V> shardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(shardingKeys, query, AggregateOperator.Max, propertySql);
    }

    public <V> Object max(ShardingKeys<V> shardingKeys, DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.max(shardingKeys, query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    public <V> Object min(ShardingKeys<V> shardingKeys, DbQuery query, String propertySql) {
        return this.singleResultBy(shardingKeys, query, AggregateOperator.Min, propertySql);
    }

    public <V> Object min(ShardingKeys<V> shardingKeys, DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.min(shardingKeys, query, LambdaUtils.getPropertyName(lambdaFunction));
    }
}
