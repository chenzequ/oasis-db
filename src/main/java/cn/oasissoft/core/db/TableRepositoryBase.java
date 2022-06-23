package cn.oasissoft.core.db;

import cn.oasissoft.core.db.executor.write.DeleteSqlExecutor;
import cn.oasissoft.core.db.executor.write.InsertSqlExecutor;
import cn.oasissoft.core.db.executor.write.UpdateSqlExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 表仓储基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 16:20
 */
public abstract class TableRepositoryBase<T, K> extends ViewRepositoryBase<T, K> {

    protected final InsertSqlExecutor<T, K> insertSE;
    protected final UpdateSqlExecutor<T, K> updateSE;
    protected final DeleteSqlExecutor<T, K> deleteSE;

    public TableRepositoryBase(NamedParameterJdbcTemplate readJdbc, NamedParameterJdbcTemplate writeJdbc) {
        super(readJdbc, writeJdbc);

        this.insertSE = new InsertSqlExecutor<>(this.getTableSchema(), this.getWriteDbType(), this::executeUpdate, this::executeBatchUpdate);
        this.updateSE = new UpdateSqlExecutor<>(this.getTableSchema(), this.getWriteDbType(), this::executeUpdate);
        this.deleteSE = new DeleteSqlExecutor<>(this.getTableSchema(), this.getWriteDbType(), this::executeUpdate);
    }

    public TableRepositoryBase(NamedParameterJdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, null);
    }

    /**
     * 新增保存
     *
     * @param model
     * @return
     */
    public int save(T model) {
        return this.insertSE.by(model);
    }

    /**
     * 更新保存
     *
     * @param model
     * @return
     */
    public int update(T model) {
        return this.updateSE.by(model);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public int remove(K id) {
        return this.deleteSE.byId(id);
    }
}
