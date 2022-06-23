package cn.oasissoft.core.db.entity.schema;

import cn.oasissoft.core.db.utils.DbReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 列结构
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 11:53
 */
public class ColumnSchema {

    private Logger logger = LoggerFactory.getLogger(ColumnSchema.class);

    private final String columnName; // 表名
    private final Field field; // 映射属性字段

    public ColumnSchema(Field field, String columnName) {
        this.columnName = columnName;
        this.field = field;
    }

    /**
     * 获取列名
     *
     * @return
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * 获取列名(Sql格式化)
     *
     * @return
     */
    public String getColumnNameSql() {
        return "`" + this.columnName + "`";
    }

    /**
     * 获取属性名
     *
     * @return
     */
    public String getProperty() {
        return this.field.getName();
    }

    /**
     * 获取属性类型
     *
     * @return
     */
    public Type getType() {
        return this.field.getType();
    }

    public boolean isDateTimeType() {
        Type type = getType();
        return type.equals(LocalDateTime.class) || type.equals(LocalDate.class) || type.equals(LocalTime.class) || type.equals(Date.class);
    }

    /**
     * 从目录对象获取属性值
     *
     * @param target
     * @return
     */
    public Object getValue(Object target) {
        return DbReflectUtils.getValue(this.field, target);
    }

    /**
     * 设置属性值到目录对象
     *
     * @param target
     * @param propertyValue
     */
    public void setValue(Object target, Object propertyValue) {
        DbReflectUtils.setValue(this.field, target, propertyValue);
    }

}
