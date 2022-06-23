package cn.oasissoft.core.db.entity.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 15:50
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {

    /**
     * 字段名
     *
     * @return
     */
    String name();

}
