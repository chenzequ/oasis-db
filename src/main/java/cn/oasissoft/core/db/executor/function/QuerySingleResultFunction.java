package cn.oasissoft.core.db.executor.function;

import java.util.Map;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:23
 */
public interface QuerySingleResultFunction {
    Object apply(String sql, Map<String, Object> params);
}

