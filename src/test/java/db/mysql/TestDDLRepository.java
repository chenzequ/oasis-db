package db.mysql;

import cn.oasissoft.core.db.DDLRepository;
import cn.oasissoft.core.db.config.RepositoryConfigParams;
import org.springframework.stereotype.Repository;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/23 12:29
 */
@Repository
public class TestDDLRepository extends DDLRepository<TestInfo,Long> {
    public TestDDLRepository(RepositoryConfigParams configParams) {
        super(configParams);
    }

}
