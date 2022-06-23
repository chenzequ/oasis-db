package cn.oasissoft.core.db.entity.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表主键
 *
 * @author Quinn
 * @desc
 * @date 2022/4/29
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTableId {
    // 主键策略
    PrimaryKeyStrategy strategy() default PrimaryKeyStrategy.None;
}
