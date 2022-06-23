package cn.oasissoft.core.db;

import cn.oasissoft.core.db.entity.PageList;
import cn.oasissoft.core.db.executor.group.GroupSqlExecutor;
import cn.oasissoft.core.db.executor.query.QueryItemSqlExecutor;
import cn.oasissoft.core.db.executor.query.QueryListSqlExecutor;
import cn.oasissoft.core.db.executor.query.QueryPageSqlExecutor;
import cn.oasissoft.core.db.executor.query.QuerySingleSqlExecutor;
import cn.oasissoft.core.db.query.DbQuery;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

/**
 * 查询数据库仓储基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 11:00
 */
public abstract class ViewRepositoryBase<T, K> extends EntityRepositoryBase<T, K> {

    protected final QueryItemSqlExecutor<T, K> itemSE;
    protected final QueryListSqlExecutor<T, K> listSE;
    protected final QueryPageSqlExecutor<T, K> pageSE;
    protected final QuerySingleSqlExecutor<T, K> singleSE;
    protected final GroupSqlExecutor<T, K> groupSE;

    protected ViewRepositoryBase(NamedParameterJdbcTemplate readJdbc, NamedParameterJdbcTemplate writeJdbc) {
        super(readJdbc, writeJdbc);
        this.itemSE = new QueryItemSqlExecutor<>(this.getTableSchema(), this.getReadDbType(), this::queryForMap);
        this.listSE = new QueryListSqlExecutor<>(this.getTableSchema(), this.getReadDbType(), this::queryForList);
        this.pageSE = new QueryPageSqlExecutor<>(this.getTableSchema(), this.getReadDbType(), this::queryForList, this::querySingleResult);
        this.singleSE = new QuerySingleSqlExecutor<>(this.getTableSchema(), this.getReadDbType(), this::querySingleResult);
        this.groupSE = new GroupSqlExecutor<>(this.getTableSchema(), this.getReadDbType(), this::queryForList);
    }

    public ViewRepositoryBase(NamedParameterJdbcTemplate readJdbc) {
        this(readJdbc, null);
    }

    /**
     * 查询对象
     *
     * @param id
     * @return
     */
    public T queryModel(K id) {
        return itemSE.toModel(id);
    }

    /**
     * 查询符合条件对象集合
     *
     * @param query
     * @return
     */
    public List<T> queryList(DbQuery query) {
        return listSE.toModels(query);
    }

    /**
     * 分页查询符合条件对象集合
     *
     * @param query
     * @param size
     * @param index
     * @return
     */
    public List<T> queryList(DbQuery query, int size, int index) {
        return listSE.toModels(query, size, index);
    }

    /**
     * 分页查询符合条件对象集合
     *
     * @param query
     * @param size
     * @param index
     * @return
     */
    public PageList<T> queryPage(DbQuery query, int size, int index) {
        return pageSE.toPageModels(query, size, index);
    }

}

