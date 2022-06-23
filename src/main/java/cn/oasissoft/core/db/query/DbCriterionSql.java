package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

/**
 * 用于自定义SQL片段判断条件
 * 生成 A=B+C 或者更复杂的Sql条件
 * 用于多条查询
 * 属性值为空时，则代表整条表达式由sqlSection组成
 * PS: 不使用参数化查询，有一定风险，需要注意Sql注入
 * Created by Administrator on 2016/11/29.
 */
public class DbCriterionSql extends DbCriterion {
    public DbCriterionSql(String sqlSection) {
        this(sqlSection, DbLoginOperator.And);
    }

    /**
     * 不设置属性时，则使用sqlSection代替整条判断条件
     * 全为Sql时，不使用参数化查询，所以有一定的注入风险，需要注意
     *
     * @param sqlSection
     * @param logicOperator
     */
    public DbCriterionSql(String sqlSection, DbLoginOperator logicOperator) {
        this(null, sqlSection, DbCriterionOperator.Equal, logicOperator);
    }

    /**
     * Sql表达式片段查询条件
     *
     * @param property    属性为空，则直接使用整段的SqlSection代理，不为空则为 [属性][表达式][Sql片段]
     * @param sqlSection  Sql片段，如果要使用属性名，则可以使用getColumnByProperty()方法获取，也可以直接输出
     * @param criterionOP 属性不为空时有效，不允许为 IN,BETWEEN,NULL
     * @param logicOP
     */
    public DbCriterionSql(String property, String sqlSection, DbCriterionOperator criterionOP, DbLoginOperator logicOP) {
        super(property, null, criterionOP, logicOP);
        if (null == sqlSection || sqlSection.length() == 0) {
            throw new NullPointerException("sqlSection");
        }
        this.sqlSection = sqlSection;
        switch (criterionOP) {
            case LeftLike:
            case RightLike:
            case In:
            case NotIn:
            case Between:
            case NotBetween:
            case Null:
            case NotNull:
                throw new OasisDbDefineException("Sql片段查询条件不支持[" + criterionOP + "]");
        }
    }

    private final String sqlSection;

    /**
     * SQL片段
     *
     * @return
     */
    public String getSqlSection() {
        return sqlSection;
    }
}
