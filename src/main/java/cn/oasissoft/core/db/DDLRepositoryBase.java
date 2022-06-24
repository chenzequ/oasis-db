package cn.oasissoft.core.db;

import cn.oasissoft.core.db.config.RepositoryConfigParams;
import cn.oasissoft.core.db.executor.ddl.ColumnDefinitionMap;
import cn.oasissoft.core.db.executor.ddl.DDLSqlExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 创建仓储 基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:33
 */
public abstract class DDLRepositoryBase<T, K> extends EntityRepositoryBase<T, K> {

    protected DDLSqlExecutor<T, K> ddlSE;

    public DDLRepositoryBase(RepositoryConfigParams configParams) {
        super(configParams);
    }

    public DDLRepositoryBase() {
        this(null);
    }

    @Override
    protected void init() {
        this.ddlSE = new DDLSqlExecutor<>(this.getTableSchema(), this.getReadDbType());
    }

    public String table_MySql_UTF8_MB4(ColumnDefinitionMap columnDefinitionMap) {
        return ddlSE.renderTableSql(columnDefinitionMap, "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    public String table_MySql_UTF8_MB4() {
        return this.table_MySql_UTF8_MB4(null);
    }


}
