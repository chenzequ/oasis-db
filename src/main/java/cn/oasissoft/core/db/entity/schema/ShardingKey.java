package cn.oasissoft.core.db.entity.schema;

/**
 * 分表键
 *
 * @author Quinn
 * @desc
 * @time 2022/06/22 11:15
 */
public class ShardingKey<K> {

    public ShardingKey(K key) {
        this.key = key;
    }

    private final K key;

    public K getKey() {
        return key;
    }

    public ShardingKeys<K> toKeys(){
        return new ShardingKeys<>(this.key);
    }
}
