package cn.oasissoft.core.db.query;

/**
 * 查询条件运算符
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 00:30
 */
public enum DbCriterionOperator {
    Equal,
    NotEqual,
    GreatThan,
    GreatThanEqual,
    LessThan,
    LessThanEqual,
    Like,
    LeftLike,
    RightLike,
    NotLike,
    In,
    NotIn,
    Between,
    NotBetween,
    Null,
    NotNull
}
