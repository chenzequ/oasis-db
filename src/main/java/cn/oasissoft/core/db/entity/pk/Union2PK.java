package cn.oasissoft.core.db.entity.pk;

/**
 * 双主键 联合主键
 *
 * @param <K1>
 * @param <K2>
 */
public class Union2PK<K1, K2> implements UnionPK {

    private K1 value1;
    private K2 value2;

    public Union2PK(K1 value1, K2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }


    public K1 getValue1() {
        return value1;
    }

    public K2 getValue2() {
        return value2;
    }

    public static <K1, K2> Union2PK<K1, K2> create(K1 value1, K2 value2) {
        return new Union2PK<>(value1, value2);
    }

    @Override
    public Object[] getIdValues() {
        return new Object[]{value1, value2};
    }
}
