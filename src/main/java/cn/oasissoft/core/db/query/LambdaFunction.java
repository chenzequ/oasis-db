package cn.oasissoft.core.db.query;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Lambda 表达式函数
 *
 * @author Quinn
 * @desc
 * @time 2022/05/19 21:40
 */
public interface LambdaFunction<T> extends Function<T, Object>, Serializable {
}
