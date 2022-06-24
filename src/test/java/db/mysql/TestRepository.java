package db.mysql;

import cn.oasissoft.core.db.TableRepositoryBase;
import cn.oasissoft.core.db.config.RepositoryConfigParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:31
 */
@Repository
public class TestRepository extends TableRepositoryBase<TestInfo,Long> {

//    public TestRepository(RepositoryConfigParams configParams) {
//        super(configParams);
//    }

}
