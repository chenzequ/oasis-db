package db.mysql;

import cn.oasissoft.core.db.DDLRepositoryBase;
import cn.oasissoft.core.db.config.RepositoryConfigParams;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/23 12:29
 */
@Repository
public class TestDDLRepository extends DDLRepositoryBase<TestInfo,Long> {
    public TestDDLRepository(RepositoryConfigParams configParams) {
        super(configParams);
    }

}
