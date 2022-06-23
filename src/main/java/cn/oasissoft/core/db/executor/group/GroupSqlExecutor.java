package cn.oasissoft.core.db.executor.group;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.aggregate.AggregatePropertyBase;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QueryForListFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * group by 执行器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 16:24
 */
public class GroupSqlExecutor<T, K> extends SqlExecutorBase {

    private final QueryForListFunction queryForList;

    public GroupSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QueryForListFunction queryForListFunc) {
        super(tableSchema, databaseType);
        Assert.notNull(queryForListFunc, "queryForListFunc is null");
        this.queryForList = queryForListFunc;
    }

    public List<MapEntity> groupBy(Set<String> groupProps, AggregatePropertyBase[] aggregateProps, DbQuery query, int limitCount) {
        return GroupSqlExecutorUtils.groupBy(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, groupProps, aggregateProps, query, limitCount);
    }

    public List<MapEntity> groupBy(Set<String> groupProps, AggregatePropertyBase[] aggregateProps, DbQuery query) {
        return this.groupBy(groupProps, aggregateProps, query, 0);
    }

    public List<MapEntity> groupBy(String groupProp, AggregatePropertyBase[] aggregateProps, DbQuery query, int limitCount) {
        Set<String> groupProps = new HashSet<>(1);
        groupProps.add(groupProp);
        return this.groupBy(groupProps, aggregateProps, query, limitCount);
    }

    public List<MapEntity> groupBy(String groupProp, AggregatePropertyBase[] aggregateProps, DbQuery query) {
        return this.groupBy(groupProp, aggregateProps, query, 0);
    }

    /**
     * 按时间分组查询
     *
     * @param groupProps          分组属性
     * @param aggregateProperties 聚合属性
     * @param timeProperty        时间属性
     * @param timeUnit            时间单位
     * @param num                 时间间隔数量
     * @param beginTime           开始时间(为空时,选择从当前时间向前取指定数量时间)
     * @param query               判断条件
     * @return
     */
    public List<MapEntity> groupTimeBy(Set<String> groupProps, AggregatePropertyBase[] aggregateProperties, String timeProperty, TimeUnit timeUnit, int num, LocalDateTime beginTime, DbQuery query) {
        return GroupSqlExecutorUtils.groupTimeBy(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, groupProps, aggregateProperties, timeProperty, timeUnit, num, beginTime, query);
    }

    public List<MapEntity> groupTimeBy(Set<String> groupProps, AggregatePropertyBase[] aggregateProperties, String timeProperty, TimeUnit timeUnit, int num, LocalDateTime beginTime) {
        return this.groupTimeBy(groupProps, aggregateProperties, timeProperty, timeUnit, num, beginTime, null);
    }

    public List<MapEntity> groupTimeBy(Set<String> groupProps, AggregatePropertyBase[] aggregateProperties, String timeProperty, TimeUnit timeUnit, int num) {
        return this.groupTimeBy(groupProps, aggregateProperties, timeProperty, timeUnit, num, null);
    }

}
