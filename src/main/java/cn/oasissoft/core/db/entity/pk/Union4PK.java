package cn.oasissoft.core.db.entity.pk;

/**
 * 四主键 联合主键
 *
 * @param <K1>
 * @param <K2>
 * @param <K3>
 * @param <K4>
 */
public class Union4PK<K1, K2, K3, K4> extends Union3PK<K1, K2, K3> {

    private K4 value4;

    public K4 getValue4() {
        return value4;
    }

    public Union4PK(K1 value1, K2 value2, K3 value3, K4 value4) {
        super(value1, value2, value3);
        this.value4 = value4;
    }

    public static <K1, K2, K3, K4> Union4PK<K1, K2, K3, K4> create(K1 value1, K2 value2, K3 value3, K4 value4) {
        return new Union4PK<>(value1, value2, value3, value4);
    }

    @Override
    public Object[] getIdValues() {
        return new Object[]{getValue1(), getValue2(), getValue3(), getValue4()};
    }
}
