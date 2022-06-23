package cn.oasissoft.core.db.utils;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.query.DbCriterion;
import cn.oasissoft.core.db.query.DbCriterionOperator;
import cn.oasissoft.core.db.query.DbCriterionSql;
import cn.oasissoft.core.db.query.DbLoginOperator;
import cn.oasissoft.core.db.query.DbOrderClause;
import cn.oasissoft.core.db.query.DbOrderOperator;
import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.query.DbSubQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 查询对象辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 22:47
 */
public class DbQueryUtils {
    /**
     * 获取 Where 条件
     *
     * @param schema
     * @param query
     * @param params
     * @param <T>
     * @return
     */
    public static <T> String getWhereSql(DatabaseType dbType, TableSchema<T> schema, DbQuery query, Map<String, Object> params) {
        if (null == query || query.isNotReturnData() || query.getSubQueryList().size() == 0) {
            return "";
        }
        if (null == params) {
            throw new NullPointerException("params");
        }
        StringBuilder sb = new StringBuilder(64);
        List<DbSubQuery> subQueries = query.getSubQueryList();
        for (int i = 0; i < subQueries.size(); i++) {
            String subQueryStr = getWhereSqlBy(dbType, query.getSubQueryList().get(i), i, schema, params);
            if (subQueryStr.length() == 0) {
                continue;
            }
            if (sb.length() == 0) {
                sb.append(subQueryStr);
            } else {
                sb.insert(0, "(")
                        .append(" ")
                        .append(getSqlStrBy(subQueries.get(i).getOp()))
                        .append(" ")
                        .append(subQueryStr)
                        .append(")");

            }
        }
        return sb.toString();
    }

    /**
     * 获取 Order 条件
     *
     * @param schema
     * @param query
     * @param <T>
     * @return
     */
    public static <T> String getOrderSql(TableSchema<T> schema, DbQuery query) {
        if (null == query || query.getOrderClauseList().size() == 0) {
            return "";
        }
        Set<String> tmp = new HashSet<>();
        StringBuilder sb = new StringBuilder(32);
        for (DbOrderClause orderClause : query.getOrderClauseList()) {
            String columnName = schema.getColumnNameSql(orderClause.getProperty());
            if (!tmp.contains(columnName)) {
                if (orderClause.getOrderOperator() == DbOrderOperator.Asc) {
                    sb.append(columnName).append(",");
                } else {
                    sb.append(columnName).append(" DESC,");
                }
                tmp.add(columnName);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static <T> String getWhereSqlBy(DatabaseType dbType, DbSubQuery subQuery, Integer firstIndex, TableSchema<T> schema, Map<String, Object> params) {
        if (subQuery.getCriteria().size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < subQuery.getCriteria().size(); i++) {
            DbCriterion criterion = subQuery.getCriteria().get(i);
            String criterionStr = getWhereSqlBy(dbType, criterion, firstIndex, i, schema, params);
            if (criterionStr.length() == 0) {
                continue;
            }
            if (sb.length() == 0) {
                sb.append("(").append(criterionStr).append(")");
            } else {
                sb.insert(0, "(")
                        .append(" ")
                        .append(getSqlStrBy(criterion.getLogicOP()))
                        .append(" ")
                        .append(criterionStr)
                        .append(")");
            }
        }
        return sb.toString();
    }

    private static <T> String getWhereSqlBy(DatabaseType dbType, DbCriterion criterion, Integer firstIndex, Integer secondIndex, TableSchema<T> schema, Map<String, Object> params) {
        if (criterion instanceof DbCriterionSql) {
            return getCriterionSqlBy((DbCriterionSql) criterion, schema, params);
        } else {
            // 标准表达式
            String columnName = schema.getColumnNameBy(criterion.getProperty()); // 列名 => f_name
            String columnNameSql = columnName.indexOf('.') > 0 ? columnName : "`" + columnName + "`"; // 带反引号列名 => `f_name`
            String columnNameWithIndex = columnName + "_" + firstIndex + "_" + secondIndex; // 带序号参数化名称 => f_name_1_1
            String namedColumnNameWithIndex = DatabaseType.getSymbol(dbType) + columnNameWithIndex; // 参数化命名名称 => :f_name_1_1
            String strReturn;
            switch (criterion.getCriterionOP()) {
                case Equal:
                    strReturn = columnNameSql + "=" + namedColumnNameWithIndex;
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case GreatThanEqual:
                    strReturn = columnNameSql + ">=" + namedColumnNameWithIndex;
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case GreatThan:
                    strReturn = columnNameSql + ">" + namedColumnNameWithIndex;
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case LessThan:
                    strReturn = columnNameSql + "<" + namedColumnNameWithIndex;
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case LessThanEqual:
                    strReturn = columnNameSql + "<=" + namedColumnNameWithIndex;
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case NotEqual:
                    strReturn = columnNameSql + "<>" + namedColumnNameWithIndex;
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case Like:
                    strReturn = columnNameSql + " LIKE CONCAT('%'," + namedColumnNameWithIndex + ",'%')";
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case LeftLike:
                    strReturn = columnNameSql + " LIKE CONCAT(" + namedColumnNameWithIndex + ",'%')";
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case RightLike:
                    strReturn = columnNameSql + " LIKE CONCAT('%'," + namedColumnNameWithIndex + ")";
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case NotLike:
                    strReturn = columnNameSql + " NOT LIKE CONCAT('%'," + namedColumnNameWithIndex + ",'%')";
                    params.put(columnNameWithIndex, criterion.getValue());
                    break;
                case In:
                case NotIn:
                    if (criterion.getValues() == null || criterion.getValues().length == 0) {
                        throw new OasisDbDefineException("参数错误，必须至少提供一个及一个以上的值数组！");
                    } else if (criterion.getValues().length == 1) {
                        // 因为只有一个参数，所以简化In运算式为=,
                        if (criterion.getCriterionOP().equals(DbCriterionOperator.In)) {
                            // 使用=号
                            strReturn = columnNameSql + "=" + namedColumnNameWithIndex;
                            params.put(columnNameWithIndex, criterion.getValue());
                        } else {
                            // 使用<>号
                            strReturn = columnNameSql + "<>" + namedColumnNameWithIndex;
                            params.put(columnNameWithIndex, criterion.getValue());
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < criterion.getValues().length; i++) {
                            String namedName = columnNameWithIndex + "_" + i;
                            sb.append(DatabaseType.getSymbol(dbType)).append(namedName).append(",");
                            params.put(namedName, criterion.getValues()[i]);
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        if (criterion.getCriterionOP() == DbCriterionOperator.In) {
                            strReturn = columnNameSql + " IN (" + sb.toString() + ")";
                        } else {
                            strReturn = columnNameSql + " NOT IN (" + sb.toString() + ")";
                        }
                    }
                    break;
                case Between:
                    strReturn = columnNameSql + " BETWEEN " + namedColumnNameWithIndex + "_1 AND " + namedColumnNameWithIndex + "_2";
                    params.put(columnNameWithIndex + "_1", criterion.getValues()[0]);
                    params.put(columnNameWithIndex + "_2", criterion.getValues()[1]);
                    break;
                case NotBetween:
                    strReturn = columnNameSql + " NOT BETWEEN " + namedColumnNameWithIndex + "_1 AND " + namedColumnNameWithIndex + "_2";
                    params.put(columnNameWithIndex + "_1", criterion.getValues()[0]);
                    params.put(columnNameWithIndex + "_2", criterion.getValues()[1]);
                    break;
                case Null:
                    strReturn = columnNameSql + " IS NULL";
                    break;
                case NotNull:
                    strReturn = columnNameSql + " IS NOT NULL";
                    break;
                default:
                    throw new OasisDbDefineException("末知运算符!");
            }

            return strReturn;
        }
    }

    private static <T> String getCriterionSqlBy(DbCriterionSql criterionSql, TableSchema<T> schema, Map<String, Object> params) {
        if (null == criterionSql.getProperty() || criterionSql.getProperty().length() == 0) {
            // 没有属性，代表SqlSection是全部的
            return criterionSql.getSqlSection();
        } else {
            String columnNameSql = schema.getColumnNameSql(criterionSql.getProperty());
            String op;
            switch (criterionSql.getCriterionOP()) {
                case Equal:
                    op = "=";
                    break;
                case NotEqual:
                    op = "<>";
                    break;
                case GreatThan:
                    op = ">";
                    break;
                case GreatThanEqual:
                    op = ">=";
                    break;
                case LessThan:
                    op = "<";
                    break;
                case LessThanEqual:
                    op = "<=";
                    break;
                case Like:
                    op = " LIKE ";
                    break;
                case NotLike:
                    op = " NOT LIKE ";
                    break;
                default:
                    throw new OasisDbDefineException("sql片段表达式不支持的运算符:" + criterionSql.getCriterionOP());
            }
            return columnNameSql + op + criterionSql.getSqlSection();
        }
    }

    private static String getSqlStrBy(DbLoginOperator logicOP) {
        switch (logicOP) {
            case And:
                return "AND";
            case Or:
                return "OR";
            default:
                throw new RuntimeException("没有实现的方法!");
        }
    }
}
