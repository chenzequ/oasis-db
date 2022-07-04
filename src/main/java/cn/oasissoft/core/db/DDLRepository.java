package cn.oasissoft.core.db;

import cn.oasissoft.core.db.config.OnlyDatabaseTypeRepositoryConfigParams;
import cn.oasissoft.core.db.config.RepositoryConfigParams;
import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.executor.ddl.ColumnDefinitionMap;
import cn.oasissoft.core.db.executor.ddl.DDLSqlExecutor;

/**
 * 创建仓储 基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:33
 */
public class DDLRepository<T, K> extends EntityRepositoryBase<T, K> {

    protected DDLSqlExecutor<T, K> ddlSE;

    public DDLRepository(RepositoryConfigParams configParams) {
        super(configParams);
    }

    public DDLRepository(DatabaseType dbType, Class<T> tClass) {
        super(new OnlyDatabaseTypeRepositoryConfigParams(dbType), tClass);
    }

    public DDLRepository() {
        this((RepositoryConfigParams) null);
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

    /**
     * 创建DDL Repository实例
     *
     * @param tClass
     * @param dbType
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> DDLRepository<T, K> create(Class<T> tClass, DatabaseType dbType) {
        return new DDLRepository<>(dbType, tClass);
    }

    /**
     * 创建MySql DDL Repository实例
     *
     * @param tClass
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> DDLRepository<T, K> mysql(Class<T> tClass) {
        return create(tClass, DatabaseType.MySql);
    }
}
