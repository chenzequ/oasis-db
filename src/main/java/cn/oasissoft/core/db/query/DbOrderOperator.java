package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

/**
 * 排序运算符
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 09:38
 */
public enum DbOrderOperator {
    Asc(0), Desc(1);

    DbOrderOperator(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static DbOrderOperator getOp(int value) {
        switch (value) {
            case 0:
                return Asc;
            case 1:
                return Desc;
            default:
                throw new OasisDbDefineException(String.format("无效的运算符值[%s]", value));
        }
    }
}
