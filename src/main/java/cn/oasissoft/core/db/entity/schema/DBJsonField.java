package cn.oasissoft.core.db.entity.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表这个字段是Json字段
 *
 * @author Quinn
 * @desc
 * @time 2022/7/20
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBJsonField {
}
