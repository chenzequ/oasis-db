package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.utils.LambdaUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库子查询对象
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 00:29
 */
public class DbSubQuery {
    private final List<DbCriterion> criteria;
    private final DbLoginOperator logicOP;

    public List<DbCriterion> getCriteria() {
        return criteria;
    }

    public DbLoginOperator getOp() {
        return logicOP;
    }

    public DbSubQuery(DbLoginOperator op) {
        this.criteria = new ArrayList<>();
        this.logicOP = op;
    }

    public DbSubQuery() {
        this(DbLoginOperator.And);
    }

    public DbSubQuery(List<DbCriterion> criteria, DbLoginOperator op) {
        if (criteria != null) {
            this.criteria = criteria;
        } else {
            this.criteria = new ArrayList<>();
        }
        this.logicOP = op;
    }

    public DbSubQuery(List<DbCriterion> criteria) {
        this(criteria, DbLoginOperator.And);
    }

    /**
     * 添加条件
     *
     * @param property
     * @param values
     * @param criterionOperator
     * @param logicOp
     * @return
     */
    public DbSubQuery add(String property, Object[] values, DbCriterionOperator criterionOperator, DbLoginOperator logicOp) {
        return add(new DbCriterion(property, values, criterionOperator, logicOp));
    }

    /**
     * 添加条件
     *
     * @param property
     * @param value
     * @param criterionOperator
     * @param logicOp
     * @return
     */
    public DbSubQuery add(String property, Object value, DbCriterionOperator criterionOperator, DbLoginOperator logicOp) {
        boolean isArray = value != null && value.getClass().isArray();
        Object[] values = null;
        if (value != null) {
            if (value instanceof List) {
                List list = (List) value;
                values = list.toArray();
            } else if (value.getClass().isArray()) {
                values = (Object[]) value;
            } else {
                values = new Object[]{value};
            }
        }
        return add(property, values, criterionOperator, logicOp);
    }

    /**
     * 添加条件(逻辑与)
     *
     * @param property
     * @param value
     * @param criterionOperator
     * @return
     */
    public DbSubQuery add(String property, Object value, DbCriterionOperator criterionOperator) {
        return add(property, value, criterionOperator, DbLoginOperator.And);
    }

    /**
     * 添加条件(判断符为相等，逻辑与)
     *
     * @param property
     * @param value
     * @return
     */
    public DbSubQuery add(String property, Object value) {
        return add(property, value, DbCriterionOperator.Equal, DbLoginOperator.And);
    }

    public DbSubQuery add(DbCriterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public <T> DbSubQuery add(LambdaFunction<T> getProperty, Object value) {
        return this.add(getProperty, value, DbCriterionOperator.Equal);
    }

    public <T> DbSubQuery add(LambdaFunction<T> getProperty, Object value, DbCriterionOperator criterionOperator) {
        return this.add(getProperty, value, criterionOperator, DbLoginOperator.And);
    }

    public <T> DbSubQuery add(LambdaFunction<T> getProperty, Object value, DbCriterionOperator criterionOperator, DbLoginOperator loginOperator) {
        String prop = LambdaUtils.getPropertyName(getProperty);
        return this.add(prop, value, criterionOperator, loginOperator);
    }
}
