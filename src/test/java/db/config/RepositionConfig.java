package db.config;

import cn.oasissoft.core.db.config.DebugRepositoryConfigParams;
import cn.oasissoft.core.db.config.RepositoryConfigParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/24 15:23
 */
@Configuration
public class RepositionConfig {

    @Bean
    public RepositoryConfigParams repositoryConfigParams(@Autowired NamedParameterJdbcTemplate jdbcTemplate){
        return new DebugRepositoryConfigParams(jdbcTemplate);
    }

}
