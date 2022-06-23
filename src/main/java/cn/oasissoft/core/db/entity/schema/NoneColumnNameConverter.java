package cn.oasissoft.core.db.entity.schema;

/**
 * 不转换的转换器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 14:54
 */
public class NoneColumnNameConverter implements ColumnNameConverter {

    public static final NoneColumnNameConverter INSTANCE = new NoneColumnNameConverter();

    private NoneColumnNameConverter() {
    }

    @Override
    public String toColumnName(String property) {
        return property;
    }
}
