package cn.oasissoft.core.db.config;

import cn.oasissoft.core.db.entity.DatabaseType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 仓储配置属性对象
 *
 * @author Quinn
 * @desc
 * @time 2022/06/24 11:31
 */
public class RepositoryConfigParams {

    private final NamedParameterJdbcTemplate readJdbc;
    private final NamedParameterJdbcTemplate writeJdbc;
    private final DatabaseType readDbType;
    private final DatabaseType writeDbType;

    protected RepositoryConfigParams(DatabaseType dbType) {
        // 用于参数输出，而不需要执行实际参数
        this.readJdbc = null;
        this.writeJdbc = null;
        this.readDbType = dbType;
        this.writeDbType = dbType;
    }

    public RepositoryConfigParams(NamedParameterJdbcTemplate jdbc) {
        this(jdbc, null);
    }

    public RepositoryConfigParams(NamedParameterJdbcTemplate readJdbc, NamedParameterJdbcTemplate writeJdbc) {
        Assert.notNull(readJdbc, "readJdbc");
        this.readJdbc = readJdbc;
        if (writeJdbc == null) {
            this.writeJdbc = readJdbc;
        } else {
            this.writeJdbc = writeJdbc;
        }
        this.readDbType = DatabaseType.getDataBaseBy(this.readJdbc);
        this.writeDbType = DatabaseType.getDataBaseBy(this.writeJdbc);
    }

    public NamedParameterJdbcTemplate getReadJdbc() {
        return readJdbc;
    }

    public NamedParameterJdbcTemplate getWriteJdbc() {
        return writeJdbc;
    }

    public DatabaseType getReadDbType() {
        return readDbType;
    }

    public DatabaseType getWriteDbType() {
        return writeDbType;
    }

    public void beforeSqlExecute(String id, String sql, Map<String, Object>[] paramsArray) {

    }

    public void afterSqlExecute(String id, String sql, Map<String, Object>[] paramsArray, Long diffTimes, Object result) {

    }

    public void sqlExecuteException(String id, String sql, Map<String, Object>[] paramsArray, Exception e) {

    }
}
