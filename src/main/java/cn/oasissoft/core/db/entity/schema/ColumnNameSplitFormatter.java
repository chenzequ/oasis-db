package cn.oasissoft.core.db.entity.schema;

import org.springframework.util.Assert;

/**
 * 列名全小写并且使用下划线分隔的列名转化器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 19:47
 */
public class ColumnNameSplitFormatter implements ColumnNameConverter {

    public static final ColumnNameSplitFormatter INSTANCE = new ColumnNameSplitFormatter();

    private ColumnNameSplitFormatter() {

    }

    @Override
    public String toColumnName(String property) {
        Assert.hasText(property, "属性名称不能为空");
        StringBuilder sb = new StringBuilder(32);
        for (char c : property.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append("_").append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toLowerCase();
    }
}
