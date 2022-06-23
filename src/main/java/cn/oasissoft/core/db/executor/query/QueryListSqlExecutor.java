package cn.oasissoft.core.db.executor.query;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import cn.oasissoft.core.db.executor.function.QueryForListFunction;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 17:22
 */
public class QueryListSqlExecutor<T, K> extends SqlExecutorBase<T, K> {

    private final QueryForListFunction queryForList;

    public QueryListSqlExecutor(TableSchema<T> tableSchema, DatabaseType databaseType, QueryForListFunction queryForList) {
        super(tableSchema, databaseType);
        Assert.notNull(queryForList, "queryForList not null");
        this.queryForList = queryForList;
    }

    public List<T> toModels(DbQuery query) {
        return toModels(query, -1, 1);
    }

    public List<T> toModels(DbQuery query, int size, int index) {
        return QuerySqlExecutorUtils.queryModels(this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, query, size, index);
    }

    public <V> List<V> toViews(Class<V> vClass, DbQuery query) {
        return toViews(vClass, query, -1, 1);
    }

    public <V> List<V> toViews(Class<V> vClass, DbQuery query, int size, int index) {
        return QuerySqlExecutorUtils.queryViews(vClass, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, query, size, index);
    }

    public List<MapEntity> toMaps(Set<String> props, DbQuery query) {
        return toMaps(props, query, -1, 1);
    }

    public List<MapEntity> toMaps(Set<String> props, DbQuery query, int size, int index) {
        return QuerySqlExecutorUtils.queryMaps(props, this.tableSchema.getTableNameSql(), this.tableSchema, this.databaseType, this.queryForList, query, size, index);
    }

}
