package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.aggregate.AggregateOperator;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
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
public class QuerySingleSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final QuerySingleResultFunction querySingleResult;

    public QuerySingleSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QuerySingleResultFunction querySingleResult) {
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
    public Object singleResultBy(DbQuery query, AggregateOperator op, String propertyOrSql) {
        return QuerySqlExecutorUtils.querySingleResult(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.querySingleResult, query, op, propertyOrSql);
    }

    public Object singleResultBy(DbQuery query, AggregateOperator op, LambdaFunction<T> lambdaFunction) {
        return singleResultBy(query, op, LambdaUtils.getPropertyName(lambdaFunction));
    }

    /**
     * 统计数量
     *
     * @param query
     * @param propertySql
     * @return
     */
    public Long count(DbQuery query, String propertySql) {
        return (Long) this.singleResultBy(query, AggregateOperator.Count, propertySql);
    }

    public Long count(DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.count(query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    public Long count(DbQuery query) {
        return this.count(query, (String) null);
    }

    /**
     * 获取单值
     *
     * @param query
     * @param propertySql
     * @return
     */
    public Object single(DbQuery query, String propertySql) {
        return this.singleResultBy(query, AggregateOperator.Single, propertySql);
    }

    public Object single(DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.single(query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    /**
     * 统计平均值
     *
     * @param query
     * @param propertySql
     * @return
     */
    public Object avg(DbQuery query, String propertySql) {
        return this.singleResultBy(query, AggregateOperator.Avg, propertySql);
    }

    public Object avg(DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.avg(query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    /**
     * 合计数量
     *
     * @param query
     * @param propertySql
     * @return
     */
    public Object sum(DbQuery query, String propertySql) {
        return this.singleResultBy(query, AggregateOperator.Sum, propertySql);
    }

    public Object sum(DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.sum(query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    public Object max(DbQuery query, String propertySql) {
        return this.singleResultBy(query, AggregateOperator.Max, propertySql);
    }

    public Object max(DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.max(query, LambdaUtils.getPropertyName(lambdaFunction));
    }

    public Object min(DbQuery query, String propertySql) {
        return this.singleResultBy(query, AggregateOperator.Min, propertySql);
    }

    public Object min(DbQuery query, LambdaFunction<T> lambdaFunction) {
        return this.min(query, LambdaUtils.getPropertyName(lambdaFunction));
    }
}
