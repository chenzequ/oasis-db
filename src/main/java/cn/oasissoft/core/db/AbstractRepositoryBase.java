package cn.oasissoft.core.db;

import cn.oasissoft.core.db.config.RepositoryConfigParams;
import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.ex.OasisJdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 仓储基类
 *
 * @author Quinn
 * @desc 最重要的类, 提供jdbc底层访问封装, 一切与jdbc相关的操作的钩子都在这个类里面拦截
 * @time 2022/06/17 17:21
 */
public abstract class AbstractRepositoryBase {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    // 仓储配置参数
    private RepositoryConfigParams configParams;

    public AbstractRepositoryBase(RepositoryConfigParams configParams) {
        this.configParams = configParams;
    }

    public AbstractRepositoryBase() {
    }

    /**
     * 初始化仓储配置参数
     *
     * @param configParams
     */
    @Autowired
    protected void setConfigParams(RepositoryConfigParams configParams) {
        if (this.configParams == null) {
            this.configParams = configParams;
        }
    }

    @PostConstruct
    private void postConstruct() {
        Assert.notNull(this.configParams, "configParams is null.");
        this.init();
    }

    // 初始化
    protected void init() {
    }


    protected DatabaseType getReadDbType() {
        return this.configParams.getReadDbType();
    }

    protected DatabaseType getWriteDbType() {
        return this.configParams.getWriteDbType();
    }

    // 抽象方法
    protected NamedParameterJdbcTemplate readJdbcTemplate() {
        return this.configParams.getReadJdbc();
    }

    /**
     * 写入数据使用的 jdbcTemplate
     *
     * @return
     */
    protected NamedParameterJdbcTemplate writeJdbcTemplate() {
        return this.configParams.getWriteJdbc();
    }

    // 辅助方法

    // 统一参数处理(用于修正参数的传入值)
    protected void handleParams(Map<String, Object> params) {
        if (null != params && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                // 去掉LocalDateTime存在的纳秒
                if (value instanceof LocalDateTime) {
                    LocalDateTime dt = (LocalDateTime) value;
                    if (dt.getNano() > 0) {
                        // 处理掉纳秒值
                        LocalDateTime newValue = dt.minusNanos(dt.getNano());
                        params.replace(entry.getKey(), newValue);
                    }
                }
            }
        }
    }

    /**
     * sql执行前钩子
     *
     * @param id          sql执行id
     * @param sql         sql语句
     * @param paramsArray 传入参数
     */
    protected void beforeExecute(String id, String sql, Map<String, Object>[] paramsArray) {
        this.configParams.beforeSqlExecute(id, sql, paramsArray);
    }

    /**
     * sql执行后钩子
     *
     * @param id        sql执行id
     * @param diffTimes 本次花费时间
     * @param result    执行结果
     */
    protected void afterExecute(String id, String sql, Map<String, Object>[] paramsArray, Long diffTimes, Object result) {
        // 显示输入,转出结果
        this.configParams.afterSqlExecute(id, sql, paramsArray, diffTimes, result);
    }

    /**
     * sql执行异常
     *
     * @param id sql执行id
     * @param e  异常
     */
    protected void executeException(String id, String sql, Map<String, Object>[] paramsArray, Exception e) {
        this.configParams.sqlExecuteException(id, sql, paramsArray, e);
    }

    /** jdbc 访问调用 **/

    // 查询

    /**
     * 执行sql查询操作
     *
     * @param sql         sql 语句
     * @param paramsArray 多Sql执行语句对应的参数
     * @param fn          主执行方法
     * @param <T>
     * @return
     */
    protected <T> T executeSql(String sql, Map<String, Object>[] paramsArray, Supplier<T> fn) {
        if (null == fn) {
            throw new NullPointerException("fn");
        }
        // 参数统一处理
        if (null != paramsArray && paramsArray.length > 0) {
            for (Map<String, Object> params : paramsArray) {
                handleParams(params);
            }
        }

        String execId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        beforeExecute(execId, sql, paramsArray);

        try {
            // sql执行
            T result = fn.get();
            // 执行后过滤
            afterExecute(execId, sql, paramsArray, System.currentTimeMillis() - start, result);
            return result;
        } catch (Exception e) {
            // 异常过滤
            executeException(execId, sql, paramsArray, e);
            throw new OasisJdbcException(e);
        }
    }

    /**
     * 查询单个返回结果
     *
     * @param sql    SQL语句
     * @param params 命名参数
     * @return 单列结果
     */
    protected Object querySingleResult(String sql, Map<String, Object> params) {
        return this.querySingleResult(sql, params, null);
    }

    protected Object querySingleResult(String sql, Map<String, Object> params, NamedParameterJdbcTemplate jdbcTemplate) {
        return executeSql(sql, new Map[]{params}, () -> {
            NamedParameterJdbcTemplate jdbc = jdbcTemplate == null ? readJdbcTemplate() : jdbcTemplate;
            SqlRowSet rowSet = jdbc.queryForRowSet(sql, params);
            if (rowSet.next()) {
                return rowSet.getObject(1);
            } else {
                return null;
            }
        });
    }

    /**
     * 查询列表返回结果
     *
     * @param sql    SQL语句
     * @param params 命名参数
     * @return 多行数据
     */
    protected List<Map<String, Object>> queryForList(String sql, Map<String, Object> params) {
        return this.queryForList(sql, params, null);
    }

    protected List<Map<String, Object>> queryForList(String sql, Map<String, Object> params, NamedParameterJdbcTemplate jdbcTemplate) {
        return executeSql(sql, new Map[]{params}, () -> {
            NamedParameterJdbcTemplate jdbc = jdbcTemplate == null ? readJdbcTemplate() : jdbcTemplate;
            return jdbc.queryForList(sql, params);
        });
    }

    /**
     * 查询对象返回结果
     *
     * @param sql    SQL语句
     * @param params 命名参数
     * @return 单行数据
     */
    protected Map<String, Object> queryForMap(String sql, Map<String, Object> params) {
        return queryForMap(sql, params, null);
    }

    protected Map<String, Object> queryForMap(String sql, Map<String, Object> params, NamedParameterJdbcTemplate jdbcTemplate) {
        return executeSql(sql, new Map[]{params}, () -> {
            NamedParameterJdbcTemplate jdbc = jdbcTemplate == null ? readJdbcTemplate() : jdbcTemplate;
            List<Map<String, Object>> list = jdbc.queryForList(sql, params);
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        });
    }

    // 更新

    //执行sql更新命令
    protected int executeUpdate(String sql, Map<String, Object> parameters, final boolean autoGenerateKey) {
        return executeSql(sql, new Map[]{parameters}, () -> {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource(parameters);
            NamedParameterJdbcTemplate writeJdbcTemplate = this.writeJdbcTemplate();
            if (!autoGenerateKey) {
                return writeJdbcTemplate.update(sql, sqlParameterSource);
            } else {
                GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
                int result = writeJdbcTemplate.update(sql, sqlParameterSource, keyHolder);

                if (result > 0) {
                    Number id = keyHolder.getKey();
                    return id.intValue();
                } else {
                    return result;
                }
            }
        });
    }

    //执行sql更新命令
    protected int executeUpdate(String sql, Map<String, Object> parameters) {
        return executeSql(sql, new Map[]{parameters}, () -> this.writeJdbcTemplate().update(sql, parameters));
    }

    //批量执行Sql更新命令
    protected int[] executeBatchUpdate(String sql, Map<String, Object>[] parameters) {
        return executeSql(sql, parameters, () -> this.writeJdbcTemplate().batchUpdate(sql, parameters));
    }

    /** END **/
}
