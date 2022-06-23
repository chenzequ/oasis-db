package cn.oasissoft.core.db.executor.function;

import java.util.List;
import java.util.Map;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:24
 */
public interface QueryForListFunction {
    List<Map<String, Object>> apply(String sql, Map<String, Object> params);
}
