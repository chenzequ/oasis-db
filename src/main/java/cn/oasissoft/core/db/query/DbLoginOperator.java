package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

/**
 * 逻辑运算符
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 00:32
 */
public enum DbLoginOperator {
    And(0),
    Or(1);

    DbLoginOperator(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static DbLoginOperator getOp(int value) {
        switch (value) {
            case 0:
                return And;
            case 1:
                return Or;
            default:
                throw new OasisDbDefineException(String.format("无效的运算符值[%s]", value));
        }
    }
}
