package cn.oasissoft.core.db.entity.schema;

import cn.oasissoft.core.db.ex.OasisDbDefineException;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 表结构构建器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 22:59
 */
public class TableSchemaBuilder {


    private final String[] primaryKeyProps;
    private String tableName;
    private boolean allFieldsMapping = true;
    private String[] exceptProperties;
    private ColumnNameConverter columnNameConverter; // 列名属性格式化器
    private final PrimaryKeyStrategy primaryKeyStrategy; // 主键生成策略

    private ShardingTable shardingTable; // 分表表名生成策略
    private Map<String, String> propertyColumnMap;  // 自定义属性名与列名映射
    private Map<String, Function<Object, Object>> setValueMap; // 自定义指定属性的数据库返回值处理

    private TableSchemaBuilder(String[] primaryKeyProps, PrimaryKeyStrategy primaryKeyStrategy) {
        if (null == primaryKeyProps || primaryKeyProps.length == 0) {
            throw new NullPointerException("primaryKeyProps");
        }
        this.primaryKeyProps = primaryKeyProps;
        this.primaryKeyStrategy = primaryKeyStrategy;
        this.shardingTable = NoneShardingTable.INSTANCE;
    }

    /**
     * 单自增主键
     *
     * @param firstPrimaryKeyProperty
     * @return
     */
    public static TableSchemaStep1Builder pkAutoIncrement(String firstPrimaryKeyProperty) {
        return new TableSchemaStep1Builder(new TableSchemaBuilder(new String[]{firstPrimaryKeyProperty}, PrimaryKeyStrategy.AutoIncrement));
    }

    public static TableSchemaStep1Builder pkSnowId(String firstPrimaryKeyProperty) {
        return new TableSchemaStep1Builder(new TableSchemaBuilder(new String[]{firstPrimaryKeyProperty}, PrimaryKeyStrategy.SnowId));
    }

    public static TableSchemaStep1Builder pkUUID(String firstPrimaryKeyProperty) {
        return new TableSchemaStep1Builder(new TableSchemaBuilder(new String[]{firstPrimaryKeyProperty}, PrimaryKeyStrategy.UUID));
    }

    /**
     * 多主键
     *
     * @param primaryKeyProps
     * @return
     */
    public static TableSchemaStep1Builder pk(String... primaryKeyProps) {
        return new TableSchemaStep1Builder(new TableSchemaBuilder(primaryKeyProps, PrimaryKeyStrategy.None));
    }

    /**
     * 设置分表策略
     * @param shardingTable
     * @return
     */
    public TableSchemaBuilder shardingTable(ShardingTable shardingTable) {
        this.shardingTable = shardingTable;
        return this;
    }

    /**
     * 全部属性都映射
     *
     * @param exceptProperties
     * @return
     */
    public TableSchemaBuilder allMapping(String... exceptProperties) {
        this.allFieldsMapping = true;
        this.exceptProperties = exceptProperties;
        return this;
    }

    /**
     * 全部属性都不映射
     *
     * @param exceptProperties
     * @return
     */
    public TableSchemaBuilder allNotMapping(String... exceptProperties) {
        this.allFieldsMapping = false;
        this.exceptProperties = exceptProperties;
        return this;
    }

    /**
     * 自定义属性名与列名映射
     *
     * @param property   属性名
     * @param columnName 列名
     * @return
     */
    public TableSchemaBuilder customField(String property, String columnName) {
        if (null == property || property.length() == 0) {
            throw new NullPointerException("property");
        }

        if (null == columnName || columnName.length() == 0) {
            throw new NullPointerException("columnName");
        }

        if (null == propertyColumnMap) {
            propertyColumnMap = new HashMap<>();
        }

        if (this.propertyColumnMap.containsKey(property)) {
            throw new OasisDbDefineException("属性名[" + property + "]已设置了自定义映射");
        }

        this.propertyColumnMap.put(property, columnName);
        return this;
    }

    /**
     * 设置指定属性 自定义数据库返回值转化为属性值
     *
     * @param property 属性名
     * @param function 自定义转化函数 [新值]fn(数据库值)
     * @return
     */
    public TableSchemaBuilder setValueFunction(String property, Function<Object, Object> function) {
        if (null == property) {
            throw new NullPointerException("property");
        }

        if (null == function) {
            throw new NullPointerException("function");
        }

        if (null == setValueMap) {
            setValueMap = new HashMap<>();
        }

        if (setValueMap.containsKey(property)) {
            throw new OasisDbDefineException("已经设置过属性[" + property + "]的自定义转化函数");
        }

        setValueMap.put(property, function);
        return this;
    }

    /**
     * 构建表结构
     *
     * @param tClass 表对应的实体类型
     * @param <T>    表对应的实体
     * @return 表结构
     */
    public <T> TableSchema<T> build(Class<T> tClass) {

        ColumnNameConverter columnNameConverter = this.columnNameConverter;
        if (columnNameConverter == null) {
            // 如果没有对列名进行独立设置，则使用默认的规则
            columnNameConverter = NoneColumnNameConverter.INSTANCE;
        }

        return new TableSchema<>(tClass, tableName, primaryKeyProps, primaryKeyStrategy, allFieldsMapping, exceptProperties, columnNameConverter,this.shardingTable, propertyColumnMap, setValueMap);
    }

    // 子步骤 1
    public static class TableSchemaStep1Builder {
        private final TableSchemaBuilder tableSchemaBuilder;

        private TableSchemaStep1Builder(TableSchemaBuilder tableSchemaBuilder) {
            this.tableSchemaBuilder = tableSchemaBuilder;
        }

        /**
         * 设置表名
         *
         * @param tableName 表名
         * @return
         */
        public TableSchemaStep2Builder tableName(String tableName) {
            if (!StringUtils.hasText(tableName)) {
                throw new NullPointerException("tableName");
            }
            tableSchemaBuilder.tableName = tableName;
            return new TableSchemaStep2Builder(tableSchemaBuilder);
        }
    }

    // 子步骤 2
    public static class TableSchemaStep2Builder {

        private final TableSchemaBuilder tableSchemaBuilder;

        private TableSchemaStep2Builder(TableSchemaBuilder tableSchemaBuilder) {
            this.tableSchemaBuilder = tableSchemaBuilder;
        }

        /**
         * 设置列名格式化器
         *
         * @param columnNameConverter
         * @return
         */
        public TableSchemaBuilder columnNameFormatter(ColumnNameConverter columnNameConverter) {
            tableSchemaBuilder.columnNameConverter = columnNameConverter;
            return tableSchemaBuilder;
        }

        /**
         * 设置列名格式化器(全小写，并且大写字母使用下划线分隔)
         *
         * @return
         */
        public TableSchemaBuilder columnNameSplitFormatter() {
            return this.columnNameFormatter(ColumnNameSplitFormatter.INSTANCE);
        }
    }
}
