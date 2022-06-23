package cn.oasissoft.core.db.utils;

import cn.oasissoft.core.db.ex.OasisDbException;
import cn.oasissoft.core.db.query.LambdaFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Lambda 辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/05/19 21:40
 */
public class LambdaUtils {

    /**
     * 获取lambda表达式函数的方法名
     *
     * @param func
     * @param <T>
     * @return
     */
    public static <T> String getMethodName(LambdaFunction<T> func) {
        try {
            Method method = method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(func);
            return lambda.getImplMethodName();
        } catch (Exception e) {
            throw new OasisDbException("lambda function fail .");
        }
    }

    /**
     * 获取lambda表达式函数对应的属性名
     *
     * @param func
     * @param <T>
     * @return
     */
    public static <T> String getPropertyName(LambdaFunction<T> func) {
        String name = getMethodName(func);
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set")) {
                throw new OasisDbException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }
}
