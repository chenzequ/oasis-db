package cn.oasissoft.core.db.executor.ddl;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.ColumnSchema;
import cn.oasissoft.core.db.entity.schema.PrimaryKeyStrategy;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.ex.OasisDbException;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 建表 sql 执行器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 16:24
 */
public class DDLSqlExecutor<T, K> extends SqlExecutorBase {

    public DDLSqlExecutor(TableSchema tableSchema, DatabaseType databaseType) {
        super(tableSchema, databaseType);
    }

    private String getColumnText(ColumnDefinition columnDefinition, String defaultText, String infoStr, Function<Integer, String> fn) {
        String sql = "";
        String typeString = columnDefinition == null ? "" : columnDefinition.getTypeString(databaseType);
        if (null != columnDefinition) {
            if (StringUtils.hasText(typeString)) {
                sql += typeString;
            } else {
                sql += fn.apply(columnDefinition.getLength());
            }
            sql += infoStr;

            if (StringUtils.hasText(columnDefinition.getComment())) {
                sql += " COMMENT '" + columnDefinition.getComment() + "'";
            }
        } else {
            sql += defaultText;
        }
        return sql;
    }

    private String buildColumnText(ColumnSchema column, ColumnDefinition columnDefinition, boolean isPK, boolean autoIncrement) {
        StringBuilder sql = new StringBuilder(32);
        sql.append(column.getColumnNameSql()).append(" ");
        String infoStr = " NULL";
        if (isPK || (null != columnDefinition && columnDefinition.isNotNull())) {
            infoStr = " NOT" + infoStr;
        }
        if (isPK && autoIncrement) {
            infoStr += " AUTO_INCREMENT ";
        }

        Type type = column.getType();
        if (String.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "varchar(255)" + infoStr, infoStr, len -> {
                if (len > 0) {
                    return "varchar(" + len + ")";
                } else {
                    return "varchar(255)";
                }
            }));
        } else if (Boolean.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "bit(1)" + infoStr, infoStr, len -> "bit(1)"));
        } else if (Byte.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "tinyint" + infoStr, infoStr, len -> "tinyint"));
        } else if (Short.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "smallint" + infoStr, infoStr, len -> "smallint"));
        } else if (Integer.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "int" + infoStr, infoStr, len -> "int"));
        } else if (Long.class.equals(type) || BigInteger.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "bigint" + infoStr, infoStr, len -> "bigint"));
        } else if (Float.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "float" + infoStr, infoStr, len -> "float"));
        } else if (Double.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "double" + infoStr, infoStr, len -> "double"));
        } else if (BigDecimal.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "decimal" + infoStr, infoStr, len -> "decimal"));
        } else if (Date.class.equals(type) || LocalDateTime.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "datetime" + infoStr, infoStr, len -> "datetime"));
        } else if (LocalDate.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "date" + infoStr, infoStr, len -> "date"));
        } else if (LocalTime.class.equals(type)) {
            sql.append(getColumnText(columnDefinition, "time " + infoStr, infoStr, len -> "time"));
        } else if (((Class) type).isEnum()) {
            sql.append(getColumnText(columnDefinition, "int" + infoStr, infoStr, len -> "int"));
        } else {
            throw new OasisDbDefineException("未知的属性类型:" + column.getProperty());
        }

        return sql.toString();
    }

    public String renderTableSql(String tableName, ColumnDefinitionMap columnDefinitionMap, String endSql) {
        if (null == columnDefinitionMap) {
            columnDefinitionMap = new ColumnDefinitionMap();
        }

        StringBuilder sql = new StringBuilder(256);

        sql.append("-- -----------------------------").append(System.lineSeparator());
        sql.append("-- table: ").append(this.tableSchema.getTableNameSql(tableName)).append(System.lineSeparator());
        sql.append("-- -----------------------------").append(System.lineSeparator());
        sql.append("DROP TABLE IF EXISTS ").append(this.tableSchema.getTableNameSql(tableName)).append(";").append(System.lineSeparator());

        sql.append("CREATE TABLE").append(this.tableSchema.getTableNameSql(tableName)).append("(");
        for (ColumnSchema column : tableSchema.getColumns()) {
            boolean isPK = false, isAutoIncrement = false;
            if (tableSchema.isPrimaryKey(column)) {
                isPK = true;
                isAutoIncrement = tableSchema.getPrimaryKeyStrategy().equals(PrimaryKeyStrategy.AutoIncrement);
            }
            sql.append(System.lineSeparator());
            sql.append(buildColumnText(column, columnDefinitionMap.get(column.getProperty()), isPK, isAutoIncrement));
            sql.append(",");
        }

        // 主键处理
        sql.append(System.lineSeparator()); // 换行
        if (tableSchema.getPrimaryKeys().length == 1) {
            sql.append("PRIMARY KEY (").append(tableSchema.getFirstPrimaryKey().getColumnNameSql()).append(")");
        } else {
            sql.append("PRIMARY KEY (");
            for (int i = 0; i < tableSchema.getPrimaryKeys().length; i++) {
                if (i != 0) {
                    sql.append(",");
                }
                sql.append(tableSchema.getPrimaryKeys()[i].getColumnNameSql());
            }
            sql.append(")");
        }

        // H2 数据库不支持索引
        if (!databaseType.equals(DatabaseType.H2)) {
            // 索引
            // INDEX [索引名] (属性(length)) -- 如果是CHAR，VARCHAR类型，length可以小于字段实际长度；如果是BLOB和TEXT类型，必须指定 length。
            // UNIQUE [索引名] (属性(length))
            // INDEX [组合索引名] (属性1(length),属性2(length))
            Map<String, DBIndex> allIndexes = columnDefinitionMap.getAllIndexes();
            if (allIndexes.size() > 0) {
                for (Map.Entry<String, DBIndex> entry : allIndexes.entrySet()) {
                    String prop = entry.getKey();
                    String field = tableSchema.getColumnNameBy(prop);
                    if (tableSchema.isPrimaryKey(prop)) {
                        throw new OasisDbDefineException("主键不需要创建索引");
                    }
                    if (entry.getValue().getType().equals(DBIndex.IndexType.UNIQUE)) {
                        sql.append(",").append(System.lineSeparator()).append("UNIQUE");
                    } else {
                        sql.append(",").append(System.lineSeparator()).append("INDEX");
                    }

                    sql.append(" `idx_" + prop.toLowerCase() + "` (`" + field).append("`");
                    if (entry.getValue().getLength() > 0) {
                        sql.append("(").append(entry.getValue().getLength()).append(")");
                    }
                    sql.append(")");
                }
            }
            // 组合索引处理
            List<DBCombineIndex[]> allCombineIndexes = columnDefinitionMap.getAllCombineIndexes();
            if (allCombineIndexes != null && allCombineIndexes.size() > 0) {
                for (DBCombineIndex[] combineIndex : allCombineIndexes) {
                    // 验证组合索引参数是否合法
                    Set<String> props = new HashSet<>();
                    ColumnDefinitionMap finalColumnDefinitionMap = columnDefinitionMap;
                    Arrays.stream(combineIndex).forEach(ci -> {
                        props.add(ci.getProperty());
                        // 验证长度是否正确
                        ColumnDefinition cd = finalColumnDefinitionMap.get(ci.getProperty());
                        if (cd != null) {
                            cd.checkIndexLength(ci.getLength());
                        }
                    });
                    if (props.size() != combineIndex.length) {
                        throw new OasisDbException("组合索引中存在相同属性");
                    }
                    if (props.size() < 2) {
                        throw new OasisDbException("组合索引必须至少有两个属性");
                    }

                    // 生成组合索引sql
                    sql.append(",").append(System.lineSeparator()).append("INDEX `idx");
                    for (DBCombineIndex index : combineIndex) {
                        sql.append("_").append(index.getProperty().toLowerCase());
                    }
                    sql.append("` (");
                    for (DBCombineIndex index : combineIndex) {
                        String field = tableSchema.getColumnNameBy(index.getProperty());
                        sql.append("`").append(field).append("`");
                        if (index.getLength() > 0) {
                            sql.append("(").append(index.getLength()).append(")");
                        }
                        sql.append(",");
                    }
                    sql.deleteCharAt(sql.length() - 1);
                    sql.append(")");
                }
            }
        }

        sql.append(System.lineSeparator()); // 换行
        sql.append(")");
        sql.append(endSql);
        sql.append(";");
        return sql.toString();
    }

    public String renderTableSql(ColumnDefinitionMap columnDefinitionMap, String endSql) {
        return renderTableSql(null, columnDefinitionMap, endSql);
    }

}
