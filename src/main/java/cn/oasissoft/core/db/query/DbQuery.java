package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.utils.LambdaUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Quinn
 * @title: 数据库查询对象类
 * @description:
 * @date: 2021-03-18 5:12 下午
 */
public class DbQuery {
    private boolean isReturnData = true;
    private final List<DbSubQuery> subQueryList;
    private final List<DbOrderClause> orderClauseList;

    public DbQuery() {
        subQueryList = new ArrayList<>();
        orderClauseList = new ArrayList<>();
    }

    /**
     * 判断条件是否不返回使用值
     * 相当于在 where 添加 1<>1的条件
     *
     * @return
     */
    public boolean isNotReturnData() {
        return !isReturnData;
    }

    public void setReturnData(boolean returnData) {
        this.isReturnData = returnData;
    }

    public List<DbSubQuery> getSubQueryList() {
        return subQueryList;
    }

    public List<DbOrderClause> getOrderClauseList() {
        return orderClauseList;
    }

    public DbQuery addCriterion(DbCriterion criterion) {
        if (subQueryList.size() == 0) {
            subQueryList.add(new DbSubQuery());
        }
        this.subQueryList.get(0).add(criterion);
        return this;
    }

    public DbQuery addCriterion(String property, Object value) {
        return addCriterion(property, value, DbCriterionOperator.Equal);
    }

    public DbQuery addCriterion(String property, Object value, DbCriterionOperator criterionOperator) {
        return addCriterion(property, value, criterionOperator, DbLoginOperator.And);
    }

    public DbQuery addCriterion(String property, Object value, DbCriterionOperator criterionOperator, DbLoginOperator logicOperator) {
        return addCriterion(new DbCriterion(property, value, criterionOperator, logicOperator));
    }

    public <T> DbQuery addCriterion(LambdaFunction<T> getProperty, Object value) {
        return this.addCriterion(getProperty, value, DbCriterionOperator.Equal);
    }

    public <T> DbQuery addCriterion(LambdaFunction<T> getProperty, Object value, DbCriterionOperator criterionOperator) {
        return this.addCriterion(getProperty, value, criterionOperator, DbLoginOperator.And);
    }

    public <T> DbQuery addCriterion(LambdaFunction<T> getProperty, Object value, DbCriterionOperator criterionOperator, DbLoginOperator loginOperator) {
        String prop = LambdaUtils.getPropertyName(getProperty);
        return this.addCriterion(prop, value, criterionOperator, loginOperator);
    }

    public DbQuery addCriteria(List<DbCriterion> criteria) {
        if (criteria != null && criteria.size() > 0) {
            DbSubQuery subQuery = new DbSubQuery();
            for (DbCriterion criterion : criteria) {
                subQuery.add(criterion);
            }
            this.subQueryList.add(subQuery);
        }
        return this;
    }

    public DbQuery addSubQuery(DbSubQuery subQuery) {
        this.subQueryList.add(subQuery);
        return this;
    }

    public DbQuery addOrder(String property, DbOrderOperator op) {
        for (DbOrderClause order : orderClauseList) {
            if (order.getProperty().toLowerCase().equals(property)) {
                throw new OasisDbDefineException("添加的排序条件属性[" + property + "]已经存在!");
            }
        }
        orderClauseList.add(new DbOrderClause(property, op));
        return this;
    }

    public DbQuery addOrder(String property) {
        return addOrder(property, DbOrderOperator.Asc);
    }

    public DbQuery addOrderDESC(String property) {
        return addOrder(property, DbOrderOperator.Desc);
    }

    public <T> DbQuery addOrder(LambdaFunction<T> getProperty, DbOrderOperator operator) {
        String prop = LambdaUtils.getPropertyName(getProperty);
        return this.addOrder(prop, operator);
    }

    public <T> DbQuery addOrder(LambdaFunction<T> getProperty) {
        return this.addOrder(getProperty, DbOrderOperator.Asc);
    }

    public <T> DbQuery addOrderDESC(LambdaFunction<T> getProperty) {
        return this.addOrder(getProperty, DbOrderOperator.Desc);
    }

    /**
     * 判断当前查询对象是否包含条件表达式
     *
     * @return
     */
    public boolean hasCriterion() {
        if (subQueryList.size() > 0) {
            for (DbSubQuery subQuery : subQueryList) {
                if (subQuery.getCriteria().size() > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断当前是否有排序条件
     *
     * @return
     */
    public boolean hasOrder() {
        return orderClauseList.size() > 0;
    }

    public static <T> DbQueryBuilder.InnerItemBuilder builderAnd(LambdaFunction<T> getProperty) {
        return DbQueryBuilder.and(getProperty);
    }

    public static <T> DbQueryBuilder.InnerItemBuilder builderOr(LambdaFunction<T> getProperty) {
        return DbQueryBuilder.or(getProperty);
    }

    public static <T> DbQueryBuilder.InnerBuilder builderOrderAsc(LambdaFunction<T> getProperty) {
        return DbQueryBuilder.orderAsc(getProperty);
    }

    public static <T> DbQueryBuilder.InnerBuilder builderOrderDesc(LambdaFunction<T> getProperty) {
        return DbQueryBuilder.orderDesc(getProperty);
    }

//    private boolean converted = false;

//    /**
//     * 查询条件属性名转化
//     */
//    public void propertyConvert(JoinViewSchema viewSchema) {
//        if (viewSchema == null) {
//            throw new NullPointerException("viewSchema");
//        }
//
//        if (converted || isNotReturnData()) {
//            return;
//        }
//
//        for (DbSubQuery subQuery : subQueryList) {
//            for (DbCriterion criterion : subQuery.getCriteria()) {
//                criterion.propertyConvert(viewSchema);
//            }
//        }
//
//        for (DbOrderClause orderClause : orderClauseList) {
//            orderClause.propertyConvert(viewSchema);
//        }
//
//        this.converted = true;
//    }
//
//    public DatabaseQuery convert(DatabaseType databaseType, TableSchema tableSchema) {
//        return new DatabaseQuery(databaseType, tableSchema, this);
//    }
}
