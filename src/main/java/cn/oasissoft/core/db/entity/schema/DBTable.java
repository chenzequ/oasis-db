package cn.oasissoft.core.db.entity.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表注解
 *
 * @author Quinn
 * @desc
 * @date 2022/4/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
    String value() default ""; // 表名

    /**
     * 列名转化器(默认为列名全小写，并且在大写字母中间_号分隔)
     *
     * @return
     */
    Class<? extends ColumnNameConverter> columnNameConverter() default ColumnNameSplitFormatter.class;

    /**
     * 分表表名生成策略
     *
     * @return
     */
    Class<? extends ShardingTable> shardingTable() default NoneShardingTable.class;
}
