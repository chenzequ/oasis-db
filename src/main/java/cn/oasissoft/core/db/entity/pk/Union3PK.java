package cn.oasissoft.core.db.entity.pk;

/**
 * 三主键 联合主键
 *
 * @param <K1>
 * @param <K2>
 * @param <K3>
 */
public class Union3PK<K1, K2, K3> extends Union2PK<K1, K2> {

    private K3 value3;

    public K3 getValue3() {
        return value3;
    }

    public Union3PK(K1 value1, K2 value2, K3 value3) {
        super(value1, value2);
        this.value3 = value3;
    }

    public static <K1, K2, K3> Union3PK<K1, K2, K3> create(K1 value1, K2 value2, K3 value3) {
        return new Union3PK<>(value1, value2, value3);
    }

    @Override
    public Object[] getIdValues() {
        return new Object[]{getValue1(), getValue2(), getValue3()};
    }
}
