package cn.oasissoft.core.db.entity.schema;

import org.springframework.util.Assert;

/**
 * 列名以f_为前缀的列名转化器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 19:52
 */
public class ColumnNameStartWithFConvert implements ColumnNameConverter {

    public static final ColumnNameStartWithFConvert INSTANCE = new ColumnNameStartWithFConvert();

    private ColumnNameStartWithFConvert() {
    }

    @Override
    public String toColumnName(String property) {
        Assert.hasText(property, "属性名称不能为空");
        StringBuilder sb = new StringBuilder(32);
        sb.append("f_");
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
