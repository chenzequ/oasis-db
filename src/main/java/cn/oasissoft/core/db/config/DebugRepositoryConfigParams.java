package cn.oasissoft.core.db.config;

import cn.oasissoft.core.db.entity.DatabaseType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

/**
 * 带debug输出的仓储配置参数
 *
 * @author Quinn
 * @desc
 * @time 2022/06/24 15:09
 */
public class DebugRepositoryConfigParams extends RepositoryConfigParams {

    protected DebugRepositoryConfigParams(DatabaseType dbType) {
        super(dbType);
    }

    public DebugRepositoryConfigParams(NamedParameterJdbcTemplate jdbc) {
        super(jdbc);
    }

    public DebugRepositoryConfigParams(NamedParameterJdbcTemplate readJdbc, NamedParameterJdbcTemplate writeJdbc) {
        super(readJdbc, writeJdbc);
    }

    @Override
    public void afterSqlExecute(String id, String sql, Map<String, Object>[] paramsArray, Long diffTimes, Object result) {
        System.out.printf("DB EXE = ========== [%s] ========== \n", id);
        System.out.printf("DB SQL = %s\n", sql);
        if (paramsArray.length > 1) {
            for (int i = 0; i < paramsArray.length; i++) {
                System.out.printf("DB >>> = [%s] %s\n", i, paramsArray[i]);
//                for (Map.Entry<String, Object> entry : paramsArray[i].entrySet()) {
//                    System.out.printf("DB >>> = [%s] KEY:[%s] = %s\n", i, entry.getKey(), entry.getValue());
//                }
            }
        } else if (paramsArray.length == 1) {
            System.out.printf("DB >>> = %s\n", paramsArray[0]);
        }

        System.out.printf("DB <<< = %s\n", result);
        System.out.printf("DB $$$ = [%s]ms\n", diffTimes);
        System.out.printf("DB OK. = ============================================================ \n");
    }

    @Override
    public void sqlExecuteException(String id, String sql, Map<String, Object>[] paramsArray, Exception e) {
        System.out.printf("DB EXE = ========== [%s] ========== \n", id);
        System.out.printf("DB SQL = %s\n", sql);
        for (int i = 0; i < paramsArray.length; i++) {
            System.out.printf("DB >>> = [%s] %s\n", i, paramsArray[i]);
//                for (Map.Entry<String, Object> entry : paramsArray[i].entrySet()) {
//                    System.out.printf("DB >>> = [%s] KEY:[%s] = %s\n", i, entry.getKey(), entry.getValue());
//                }
        }
        System.out.printf("DB *** = [%s]\n", e.getMessage());
        System.out.printf("DB FAI = ============================================================ \n");
    }
}
