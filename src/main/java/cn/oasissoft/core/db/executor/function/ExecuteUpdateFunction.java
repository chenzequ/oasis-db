package cn.oasissoft.core.db.executor.function;

import java.util.Map;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:22
 */
public interface ExecuteUpdateFunction {
    int apply(String sql, Map<String, Object> parameters);
}
