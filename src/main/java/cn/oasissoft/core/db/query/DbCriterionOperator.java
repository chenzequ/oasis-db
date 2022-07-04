package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

import java.awt.image.DataBuffer;

/**
 * 查询条件运算符
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 00:30
 */
public enum DbCriterionOperator {
    Equal(0),
    NotEqual(1),
    GreatThan(2),
    GreatThanEqual(3),
    LessThan(4),
    LessThanEqual(5),
    Like(6),
    NotLike(7),
    LeftLike(8),
    RightLike(9),
    In(10),
    NotIn(11),
    Between(12),
    NotBetween(13),
    Null(14),
    NotNull(15);

    DbCriterionOperator(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static DbCriterionOperator getOp(int value) {
        switch (value) {
            case 0:
                return DbCriterionOperator.Equal;
            case 1:
                return DbCriterionOperator.NotEqual;
            case 2:
                return DbCriterionOperator.GreatThan;
            case 3:
                return DbCriterionOperator.GreatThanEqual;
            case 4:
                return DbCriterionOperator.LessThan;
            case 5:
                return DbCriterionOperator.LessThanEqual;
            case 6:
                return DbCriterionOperator.Like;
            case 7:
                return DbCriterionOperator.NotLike;
            case 8:
                return DbCriterionOperator.LeftLike;
            case 9:
                return DbCriterionOperator.RightLike;
            case 10:
                return DbCriterionOperator.In;
            case 11:
                return DbCriterionOperator.NotIn;
            case 12:
                return DbCriterionOperator.Between;
            case 13:
                return DbCriterionOperator.NotBetween;
            case 14:
                return DbCriterionOperator.Null;
            case 15:
                return DbCriterionOperator.NotNull;
            default:
                throw new OasisDbDefineException(String.format("无效的运算符值[%s]", value));
        }
    }
}
