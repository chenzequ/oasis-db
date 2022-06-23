package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 数据库查询条件
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 00:29
 */
public class DbCriterion {

    private String property; // 属性
    private final DbCriterionOperator criterionOP; // 运算符
    private final Object[] values; // 值
    private final DbLoginOperator logicOP; // 逻辑运算符

    public DbCriterion(String property, Object value) {
        this(property, value, DbCriterionOperator.Equal);
    }

    public DbCriterion(String property, Object value, DbCriterionOperator criterionOP) {
        this(property, value, criterionOP, DbLoginOperator.And);
    }

    public DbCriterion(String property, Object value, DbCriterionOperator criterionOP, DbLoginOperator logicOP) {
        if (null == property || property.length() == 0) {
            throw new NullPointerException("property");
        }

        if (null == value) {
            // 值为空时，仅Null运算符有效
            values = null;
            if (!DbCriterionOperator.Null.equals(criterionOP) && !DbCriterionOperator.NotNull.equals(criterionOP)) {
                throw new OasisDbDefineException("运算符[" + criterionOP + "]的值不允许为空");
            }
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (0 == length) {
                throw new OasisDbDefineException("属性[" + property + "]不允许查询空数组值");
            }
            this.values = new Object[length];
            for (int i = 0; i < length; i++) {
                this.values[i] = Array.get(value, i);
            }
        } else if (value instanceof Iterable) {
            Iterable iterable = (Iterable) value;
            ArrayList al = new ArrayList();
            for (Object o : iterable) {
                al.add(o);
            }
            this.values = al.toArray();
        } else {
            this.values = new Object[]{value};
        }
        this.property = property;
        this.criterionOP = criterionOP;
        this.logicOP = logicOP;
    }

    /**
     * 获取条件表达式对应属性
     *
     * @return
     */
    public String getProperty() {
        return property;
    }

    /**
     * 获取条件表达式对应运算符
     *
     * @return
     */
    public DbCriterionOperator getCriterionOP() {
        return criterionOP;
    }

    /**
     * 获取条件表达式对应值集合
     *
     * @return
     */
    public Object[] getValues() {
        return values;
    }

    /**
     * 获取条件表达式对应的逻辑运算符
     *
     * @return
     */
    public DbLoginOperator getLogicOP() {
        return logicOP;
    }

    /**
     * 获取值
     *
     * @return
     */
    public Object getValue() {
        if (null == values || values.length == 0) {
            return null;
        } else if (values.length == 1) {
            return values[0];
        } else {
            return values;
        }
    }

}
