package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

/**
 * 算术运算符
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 09:39
 */
public enum DbOperandOperator {
    Add(1), Subtract(2), Multiply(3), Divide(4);

    DbOperandOperator(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static DbOperandOperator getOp(int value) {
        switch (value) {
            case 1:
                return Add;
            case 2:
                return Subtract;
            case 3:
                return Multiply;
            case 4:
                return Divide;
            default:
                throw new OasisDbDefineException(String.format("无效的运算符值[%s]", value));
        }
    }
}
