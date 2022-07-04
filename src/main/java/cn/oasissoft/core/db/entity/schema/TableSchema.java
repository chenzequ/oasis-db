package cn.oasissoft.core.db.entity.schema;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.pk.UnionPK;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.ex.OasisDbException;
import cn.oasissoft.core.db.utils.DbReflectUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 表结构
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 11:51
 */
public class TableSchema<T> {

    private final Class<T> entityClass; // 表结构对应的实体类
    private final String tableName; // 表名
    private final PrimaryKeyStrategy primaryKeyStrategy; // 单主键时的主键生成策略

    private final ShardingTable shardingTable;  // 数据库分表策略
    private final ColumnSchema[] pkColumns; // 主键列
    private final ColumnSchema[] columns; // 全部列
    private final Map<String, ColumnSchema> propertyMap; // Map<属性名,列>
    private final Map<String, ColumnSchema> columnMap; // Map<列名,列>
    private final Map<String, Function<Object, Object>> setValueMap; // Map<属性,自定义数据库返回值转成属性值函数>

    /**
     * 表结构构造函数
     *
     * @param entityClass           对应实体类
     * @param tableName             表名
     * @param pkProps               主键属性
     * @param pkStrategy            单主键的主键值生成策略
     * @param allFieldsMapping      是否全部属性都映射起来
     * @param exceptProps           排除的属性(与上面的allFieldsMapping互补)
     * @param converter             属性名与列名转化器
     * @param shardingTable         分表表名生成策略
     * @param propertyColumnNameMap 自定义属性名与列名映射
     * @param setValueMap           自定义数据库返回值转属性值
     */
    TableSchema(Class<T> entityClass, String tableName, String[] pkProps, PrimaryKeyStrategy pkStrategy, boolean allFieldsMapping, String[] exceptProps, ColumnNameConverter converter, ShardingTable shardingTable, Map<String, String> propertyColumnNameMap, Map<String, Function<Object, Object>> setValueMap) {
        Assert.notNull(entityClass, "[entityClass] is null.");
        Assert.hasText(tableName, "[tableName] is blank.");
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.primaryKeyStrategy = pkStrategy == null ? PrimaryKeyStrategy.None : pkStrategy;
        this.shardingTable = shardingTable;
        this.columnMap = new HashMap<>();
        this.propertyMap = new HashMap<>();

        // 主键有效性判断
        if (pkProps == null || pkProps.length < 1) {
            throw new OasisDbDefineException(String.format("[%s] 未配置主键.", entityClass.getName()));
        }
        if (this.primaryKeyStrategy != PrimaryKeyStrategy.None && pkProps.length > 1) {
            throw new OasisDbDefineException(String.format("[%s] 主键生成策略只适用于单主键.", entityClass.getName()));
        }
        Set<String> tmpSet = new HashSet<>(pkProps.length);
        for (String primaryKey : pkProps) {
            if (tmpSet.contains(primaryKey)) {
                throw new OasisDbDefineException(String.format("[%s] 存在重复主键 [%s]", entityClass.getName(), primaryKey));
            } else {
                tmpSet.add(primaryKey);
            }
        }

        // 列名格式化器
        ColumnNameConverter columnNameConverter = converter == null ? NoneColumnNameConverter.INSTANCE : converter;

        // 排除的属性
        Set<String> exceptPropsSet = new HashSet<>();
        if (null != exceptProps) {
            exceptPropsSet.addAll(Arrays.asList(exceptProps));
        }

        // 获取属性字段
        List<Field> fields = DbReflectUtils.getAllPrivateFields(entityClass);
        if (null == propertyColumnNameMap) {
            propertyColumnNameMap = new HashMap<>();
        }
        List<ColumnSchema> tempColumns = new ArrayList<>();
        for (Field field : fields) {
            // 设置私有字段可访问
            field.setAccessible(true);
            String property = field.getName();
            // 判断该属性是否需要映射
            if ((allFieldsMapping && !exceptPropsSet.contains(property)) || (!allFieldsMapping && exceptPropsSet.contains(property))) {
                String columnName;
                if (propertyColumnNameMap.containsKey(property)) {
                    // 如果在自定义的属性名与列名映射中
                    columnName = propertyColumnNameMap.get(property);
                } else {
                    // 使用属性名格式转化器转化成为列名
                    columnName = columnNameConverter.toColumnName(property);
                }
                ColumnSchema columnSchema = new ColumnSchema(field, columnName);
                tempColumns.add(columnSchema);
                // 类属性名与列结构映射
                this.propertyMap.put(columnSchema.getProperty(), columnSchema);
                // 数据库列名(小写)与列结构映射
                this.columnMap.put(columnSchema.getColumnName().toLowerCase(), columnSchema);
            }
        }
        if (tempColumns.size() == 0) {
            throw new OasisDbDefineException(String.format("[%s]没有定义列属性.", entityClass.getName()));
        }

        // 处理列
        this.columns = tempColumns.toArray(new ColumnSchema[0]);

        // 处理主键列
        this.pkColumns = new ColumnSchema[pkProps.length];
        for (int i = 0; i < pkProps.length; i++) {
            String pk = pkProps[i];
            if (propertyMap.containsKey(pk)) {
                ColumnSchema column = propertyMap.get(pk);
                if (this.primaryKeyStrategy != PrimaryKeyStrategy.None && !this.primaryKeyStrategy.isValid(column.getType())) {
                    throw new OasisDbDefineException(String.format("类[%s] 主键[%s]的类型[%s]不能用于主键生成策略[%s]", entityClass.getName(), pk, column.getType(), this.primaryKeyStrategy));
                }
                this.pkColumns[i] = column;
            } else {
                throw new OasisDbDefineException(String.format("类[%s]不存在主键[%s]", entityClass.getName(), pk));
            }
        }

        this.setValueMap = setValueMap != null ? setValueMap : null;
    }

    /**
     * 判断属性是否是主键
     *
     * @param property
     * @return
     */
    public boolean isPrimaryKey(String property) {
        return Arrays.stream(pkColumns).anyMatch(col -> col.getProperty().equals(property));
    }

    /**
     * 判断列定义是否是主键
     *
     * @param column
     * @return
     */
    public boolean isPrimaryKey(ColumnSchema column) {
        return Arrays.stream(pkColumns).anyMatch(col -> col.equals(column));
    }

    /**
     * 获取主键生成策略
     * @return
     */
    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return primaryKeyStrategy;
    }

    /**
     * 是否是主增主键
     * @return
     */
    public boolean isAutoIncrement(){
        return this.pkColumns.length == 1 && this.primaryKeyStrategy == PrimaryKeyStrategy.AutoIncrement;
    }

    /**
     * 获取当前表的全部列
     * @return
     */
    public ColumnSchema[] getColumns() {
        return columns;
    }
    /**
     * 通过列名获取对应的列
     *
     * @param columnName 列名(小写)
     * @return 无法找到时返回 null
     */
    public ColumnSchema getColumnByColumnName(String columnName) {
        Assert.hasText(columnName, "column name is blank.");
        return columnMap.getOrDefault(columnName.toLowerCase(), null);
    }

    /**
     * 根据属性名获取对应的列
     *
     * @param property
     * @return
     */
    public ColumnSchema getColumnByProperty(String property) {
        Assert.hasText(property, "property is blank.");
        return propertyMap.getOrDefault(property, null);
    }

    /**
     * 获取表名
     *
     * @return
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * 获取表名sql
     *
     * @return
     */
    public String getTableNameSql() {
        return this.getTableNameSql(null);
    }

    public String getTableNameSql(String customTableName) {
        if (StringUtils.hasText(customTableName)) {
            // 判断表名是否有空格
            if (customTableName.indexOf(' ') > 0) {
                // 存在空格说明tableName是一个表语句,则直接返回
                return customTableName;
            } else {
                return "`" + customTableName + "`";
            }
        } else {
            return "`" + this.getTableName() + "`";
        }
    }

    /**
     * 根据属性名获取对应的列名(不含反引号)
     *
     * @param property
     * @return
     */
    public String getColumnNameBy(String property) {
        return propertyMap.containsKey(property) ? propertyMap.get(property).getColumnName() : property;
    }

    public String getColumnNameSql(String property) {
        if (propertyMap.containsKey(property)) {
            return propertyMap.get(property).getColumnNameSql();
        } else {
            int index = property.indexOf('.');
            if (index > 0) {
                return "`" + property.substring(0, index) + "__" + property.substring(index + 1) + "`";
            } else {
                throw new OasisDbDefineException("无法找到属性[" + property + "]对应的列名");
            }
        }
    }

    /**
     * 获取实体类型
     *
     * @return
     */
    public Class getEntityClass() {
        return this.entityClass;
    }

    /**
     * 获取主键
     *
     * @return
     */
    public ColumnSchema[] getPrimaryKeys() {
        return this.pkColumns;
    }

    /**
     * 获取首个主键
     *
     * @return
     */
    public ColumnSchema getFirstPrimaryKey() {
        return this.pkColumns[0];
    }

    /**
     * 获取全部字段的sql
     * 例: fieldA,fieldB,fieldC
     * 如果props中间出现.则不需要转化，直接输出，并且AS为双下划线(__)分隔
     * 例如 A.f_name 输出为: A.f_name AS A__f_name
     *
     * @return
     */
    public String allColumnsSql(Set<String> props) {
        StringBuilder sql = new StringBuilder(64);

        if (null == props || props.size() == 0) {
            // 全部字段输出
            for (ColumnSchema column : columns) {
                sql.append(column.getColumnNameSql()).append(",");
            }
        } else {
            for (String prop : props) {
                // 1. 列
                // 2. 子表字段(带 . ) 例如 A.f_name => A.f_name AS A__f_name
                if (propertyMap.containsKey(prop)) {
                    sql.append(propertyMap.get(prop).getColumnNameSql()).append(",");
                } else {
                    int index = prop.indexOf(".");
                    if (index > 0) {
                        sql.append(prop).append(" AS ").append(prop.replace(".", "__")).append(",");
                    } else {
                        throw new OasisDbException("属性[" + prop + "]名称格式错误");
                    }
                }
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        return sql.toString();
    }

    /**
     * 通过数据库字段映射结果返回实体对象
     *
     * @param dbMap
     * @return
     */
    public T loadModelByColumnName(Map<String, Object> dbMap) {
        Assert.notNull(dbMap, "dbMap is null.");
        T instance;
        try {
            instance = this.entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OasisDbException(String.format("[%s]实例化错误.", this.entityClass.getName()));
        }
        for (Map.Entry<String, Object> entry : dbMap.entrySet()) {
            ColumnSchema column = getColumnByColumnName(entry.getKey());
            Object value = this.setValueMap != null && this.setValueMap.containsKey(column.getProperty()) ? this.setValueMap.get(column.getProperty()).apply(entry.getValue()) : entry.getValue();
            column.setValue(instance, value);
        }
        return instance;
    }

    /**
     * 将数据库查询结果转化视图实体
     *
     * @param vClass
     * @param fieldMap
     * @param dbMap
     * @param <V>
     * @return
     */
    public <V> V convertToEntity(Class<V> vClass, Map<String, Field> fieldMap, Map<String, Object> dbMap) {
        if (dbMap == null) {
            return null;
        }
        V entity;
        try {
            entity = vClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OasisDbException(e);
        }

        for (Map.Entry<String, Object> entry : dbMap.entrySet()) {
            ColumnSchema column = getColumnByColumnName(entry.getKey());
            if (null != setValueMap && setValueMap.containsKey(column.getProperty())) {
                // 自定义返回格式
                Function<Object, Object> setValue = setValueMap.get(column.getProperty());
                Object value = setValue.apply(entry.getValue());
                DbReflectUtils.setValue(fieldMap.get(column.getProperty()), entity, value);
            } else {
                DbReflectUtils.setValue(fieldMap.get(column.getProperty()), entity, entry.getValue());
            }
        }
        return entity;
    }

    /**
     * 将数据库查询结果转成MapEntity
     *
     * @param dbMap [字段名,字段值]
     * @return
     */
    public MapEntity convertToMapEntity(Map<String, Object> dbMap) {
        Assert.notNull(dbMap, "dbMap is null.");
        MapEntity mapEntity = new MapEntity();
        for (Map.Entry<String, Object> entry : dbMap.entrySet()) {
            // 1. 类属性 f_name=>name
            // 2. 子类属性(__) a__f_name => a.f_name
            // 3. 聚合属性(as 直接输出) name=> name
            ColumnSchema column = getColumnByColumnName(entry.getKey());
            if (column == null) {
                // 标准属性处理
                Object value = setValueMap == null && setValueMap.containsKey(column.getProperty()) ? setValueMap.get(column.getProperty()).apply(entry.getValue()) : entry.getValue();
                mapEntity.put(column.getProperty(), value);
            } else {
                // 非标准属性处理
                if (entry.getKey().indexOf("__") > 0) {
                    // 子属性处理
                    mapEntity.put(entry.getKey().replace("__", "."), entry.getValue());
                } else {
                    mapEntity.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return mapEntity;
    }

    /**
     * 生成指定实体类的表结构
     *
     * @param kClass
     * @param <K>
     * @return
     */
    public static <K> TableSchema<K> renderTableSchema(Class<K> kClass) {
        DBTable dbTable = kClass.getAnnotation(DBTable.class);
        String tableName = kClass.getSimpleName();
        ShardingTable shardingTable = null;
        ColumnNameConverter converter = null;
        if (dbTable != null) {
            if (StringUtils.hasText(dbTable.value())) {
                tableName = dbTable.value();
            }
            if (!dbTable.shardingTable().equals(NoneShardingTable.class)) {
                try {
                    shardingTable = dbTable.shardingTable().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new OasisDbDefineException(String.format("创建分表表名策略[%s]对象错误，请确保该类有无参构造函数.", dbTable.shardingTable().getName()));
                }
            }
            if (dbTable.columnNameConverter().equals(NoneColumnNameConverter.class)) {
                converter = NoneColumnNameConverter.INSTANCE;
            } else if (dbTable.columnNameConverter().equals(ColumnNameSplitFormatter.class)) {
                converter = ColumnNameSplitFormatter.INSTANCE;
            } else if (dbTable.columnNameConverter().equals(ColumnNameStartWithFConvert.class)) {
                converter = ColumnNameStartWithFConvert.INSTANCE;
            } else {
                try {
                    converter = dbTable.columnNameConverter().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new OasisDbDefineException(String.format("创建表名转化器[%s]对象错误，请确保该类有无参构造函数.", dbTable.columnNameConverter().getName()));
                }
            }
        }
        List<String> exceptProps = new ArrayList<>();
        Map<String, String> propertyColumnMap = new HashMap<>();
        List<Field> fields = DbReflectUtils.getAllPrivateFields(kClass);
        List<String> pkProps = new ArrayList<>();
        PrimaryKeyStrategy strategy = PrimaryKeyStrategy.None;
        for (Field field : fields) {
            if (field.getAnnotation(DBNoField.class) != null) {
                exceptProps.add(field.getName());
                continue;
            }

            DBField dbField = field.getAnnotation(DBField.class);
            if (dbField != null) {
                propertyColumnMap.put(field.getName(), dbField.name());
            }

            DBTableId dbTableId = field.getAnnotation(DBTableId.class);
            if (dbTableId != null) {
                pkProps.add(field.getName());
                strategy = dbTableId.strategy();
            }
        }

        return new TableSchema<>(kClass, tableName, pkProps.toArray(new String[0]), strategy, true, exceptProps.toArray(new String[0]), converter, shardingTable, propertyColumnMap, null);
    }

    /**
     * 检查传入的主键类型是否与定义的一致
     *
     * @param primaryKeyType
     */
    public void checkPrimaryKeyType(Type primaryKeyType) {
        if (primaryKeyType.getClass().equals(Class.class)) {
            // 单主键类型
            if (!primaryKeyType.equals(this.getFirstPrimaryKey().getType())) {
                // 主键类型与结构定义类型不一致
                throw new OasisDbDefineException(String.format("[%s]主键类型[%s]与传入的验证类型[%s]不一致", this.entityClass.getName(), this.getFirstPrimaryKey().getType().getTypeName(), primaryKeyType.getTypeName()));
            }
        } else {
            // 联合主键类型
            if (primaryKeyType instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) primaryKeyType).getRawType();
                if (UnionPK.class.isAssignableFrom((Class<?>) rawType)) {
                    // 复合主键
                    Type[] pkTypes = ((ParameterizedType) primaryKeyType).getActualTypeArguments();
                    if (pkTypes.length != this.getPrimaryKeys().length) {
                        throw new OasisDbDefineException(String.format("类[%s]定义的主键数量[%s]与实际泛型定义的数量[%s]不一致.", this.getEntityClass(), this.getPrimaryKeys().length, pkTypes.length));
                    } else {
                        for (int i = 0; i < pkTypes.length; i++) {
                            if (!(this.getPrimaryKeys()[i].getType().equals(pkTypes[i]))) {
                                throw new OasisDbDefineException(String.format("类[%s]主键位置[%s]定义的类型[]与泛型该位置的类型[]不一致", this.getEntityClass(), this.getPrimaryKeys()[i].getType(), pkTypes[i]));
                            }
                        }
                    }
                } else {
                    throw new OasisDbDefineException(String.format("类[%s]定义的主键[%s]与泛型定义的主键[%s]不一致", this.getEntityClass(), Arrays.stream(this.getPrimaryKeys()).map(pk -> pk.getType().getTypeName()).collect(Collectors.joining(",")), primaryKeyType.getTypeName()));
                }
            } else {
                throw new OasisDbDefineException(String.format("类[%s]定义的主键[%s]与泛型定义的主键[%s]不一致", this.getEntityClass(), Arrays.stream(this.getPrimaryKeys()).map(pk -> pk.getType().getTypeName()).collect(Collectors.joining(",")), primaryKeyType.getTypeName()));
            }
        }
    }

    /**
     * @param vClass
     * @param fieldMap
     * @param dbMap
     * @param <V>
     * @return
     */
    public <V> V convertDbMapToEntity(Class<V> vClass, Map<String, Field> fieldMap, Map<String, Object> dbMap) {
        if (dbMap == null) {
            return null;
        }
        V entity;
        try {
            entity = vClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OasisDbException(String.format("创建对象[%s]失败.",vClass.getName()));
        }

        for (Map.Entry<String, Object> entry : dbMap.entrySet()) {
            ColumnSchema column = getColumnByColumnName(entry.getKey());
            if (null != setValueMap && setValueMap.containsKey(column.getProperty())) {
                // 自定义返回格式
                DbReflectUtils.setValue(fieldMap.get(column.getProperty()), entity, setValueMap.get(column.getProperty()).apply(entry.getValue()));
            } else {
                DbReflectUtils.setValue(fieldMap.get(column.getProperty()), entity, entry.getValue());
            }

        }
        return entity;
    }

    /**
     * @param dbMap
     * @return
     */
    public MapEntity convertDbMapToPropertyMap(Map<String, Object> dbMap) {
        // 如果列名没有找到，
        // 判断是否出现双下划线，如果有则改成. 例如 A__f_name=> a.f_name
        // 直接返回 AS 之后的属性

        if (dbMap == null) {
            return null;
        }
        MapEntity mapEntity = new MapEntity();
        for (Map.Entry<String, Object> entry : dbMap.entrySet()) {
            // 1. 类属性 例 f_name => name
            // 2. 子类属性(__双下划线) 例 A__f_name => a.f_name
            // 3. 聚合属性(AS) 直接输出 例 name => name
            ColumnSchema column = getColumnByColumnName(entry.getKey());
            if (null != column) {
                if (null != setValueMap && setValueMap.containsKey(column.getProperty())) {
                    // 自定义返回格式
                    mapEntity.put(column.getProperty(), setValueMap.get(column.getProperty()).apply(entry.getValue()));
                } else {
                    mapEntity.put(column.getProperty(), entry.getValue());
                }
            } else {
                if (entry.getKey().indexOf("__") > 0) {
                    mapEntity.put(entry.getKey().replace("__", "."), entry.getValue());
                } else {
                    mapEntity.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return mapEntity;
    }

    /**
     * 生成id参数
     * @param id
     * @return
     */
    public Map<String, Object> getParamArrayById(Object id) {
        Map<String, Object> params = new HashMap<>(this.pkColumns.length);
        if (id instanceof UnionPK) {
            UnionPK unionPK = (UnionPK) id;
            Object[] idValues = unionPK.getIdValues();
            for (int i = 0; i < idValues.length; i++) {
                params.put(this.getPrimaryKeys()[i].getColumnName(), idValues[i]);
            }
        }else{
            params.put(this.getFirstPrimaryKey().getColumnName(), id);
        }
        return params;
    }

    /**
     * 生成id查询条件
     * @param dbType
     * @return
     */
    public String getIdsSql(DatabaseType dbType) {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < this.getPrimaryKeys().length; i++) {
            if (i != 0) {
                sb.append(" AND ");
            }
            sb.append(this.getPrimaryKeys()[i].getColumnNameSql()).append("=").append(DatabaseType.getSymbol(dbType)).append(this.getPrimaryKeys()[i].getColumnName());
        }
        return sb.toString();
    }
    /**
     * 获取分表表名策略
     * @return
     */
    public ShardingTable getShardingTable(){
        return this.shardingTable;
    }
}
