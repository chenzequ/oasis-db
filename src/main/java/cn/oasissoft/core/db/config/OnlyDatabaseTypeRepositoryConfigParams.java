package cn.oasissoft.core.db.config;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 仅有数据库类型的仓储配置参数
 *
 * @author Quinn
 * @desc
 * @time 2022/06/25 10:59
 */
public class OnlyDatabaseTypeRepositoryConfigParams extends RepositoryConfigParams {

    public OnlyDatabaseTypeRepositoryConfigParams(DatabaseType dbType) {
        super(dbType);
    }

    @Override
    public NamedParameterJdbcTemplate getReadJdbc() {
        throw new OasisDbDefineException("only database type.");
    }

    @Override
    public NamedParameterJdbcTemplate getWriteJdbc() {
        return this.getReadJdbc();
    }
}
