package db.mysql;

import cn.oasissoft.core.db.TableRepositoryBase;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:31
 */
@Repository
public class TestRepository extends TableRepositoryBase<TestInfo,Long> {

    @Override
    protected Boolean debug() {
        return true;
    }

    public TestRepository(NamedParameterJdbcTemplate readJdbc, NamedParameterJdbcTemplate writeJdbc) {
        super(readJdbc, writeJdbc);
    }
}
