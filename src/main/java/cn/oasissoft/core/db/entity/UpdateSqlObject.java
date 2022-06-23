package cn.oasissoft.core.db.entity;

import cn.oasissoft.core.db.query.LambdaFunction;
import cn.oasissoft.core.db.utils.LambdaUtils;

import java.util.Map;

/**
 * 更新sql对象
 *
 * @author Quinn
 * @desc
 * @time 2022/06/21 16:39
 */
public class UpdateSqlObject<T> {

    private final String prop;
    private final String sql;
    private final Map<String, Object> params;

    public UpdateSqlObject(String prop, String sql, Map<String, Object> params) {
        this.prop = prop;
        this.sql = sql;
        this.params = params;
    }

    public UpdateSqlObject(String prop, String sql) {
        this(prop, sql, null);
    }

    public UpdateSqlObject(LambdaFunction<T> lambda, String sql, Map<String, Object> params) {
        this(LambdaUtils.getPropertyName(lambda), sql, params);
    }

    public UpdateSqlObject(LambdaFunction<T> lambda, String sql) {
        this(lambda, sql, null);
    }

    public String getProp() {
        return prop;
    }

    public String getSql() {
        return sql;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
