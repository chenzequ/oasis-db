package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.PageList;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QueryForListFunction;
import cn.oasissoft.core.db.executor.function.QuerySingleResultFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * 分页查询 sql 执行器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/20 17:22
 */
public class QueryPageSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final QueryForListFunction queryForList;
    private final QuerySingleResultFunction querySingleResult;

    public QueryPageSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QueryForListFunction queryForList, QuerySingleResultFunction querySingleResult) {
        super(tableSchema, databaseType);
        Assert.notNull(queryForList, "queryForList is null");
        this.queryForList = queryForList;
        Assert.notNull(querySingleResult, "querySingleResult is null");
        this.querySingleResult = querySingleResult;
    }

    /**
     * 根据 查询条件 分页查询 实体集合
     *
     * @param query
     * @param size
     * @param index
     * @return
     */
    public PageList<T> toPageModels(DbQuery query, int size, int index) {
        return QuerySqlExecutorUtils.queryPageModels(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, this.querySingleResult, query, size, index);
    }

    /**
     * 根据 查询条件 分页查询 视图集合
     *
     * @param vClass
     * @param query
     * @param size
     * @param index
     * @param <V>
     * @return
     */
    public <V> PageList<V> toPageViews(Class<V> vClass, DbQuery query, int size, int index) {
        return QuerySqlExecutorUtils.queryPageViews(vClass, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, this.querySingleResult, query, size, index);
    }

    /**
     * 根据 查询条件 分页查询 map集合
     *
     * @param props
     * @param query
     * @param size
     * @param index
     * @return
     */
    public PageList<MapEntity> toPageMaps(Set<String> props, DbQuery query, int size, int index) {
        return QuerySqlExecutorUtils.queryPageMaps(props, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, this.querySingleResult, query, size, index);
    }
}
