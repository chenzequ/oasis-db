package cn.oasissoft.core.db.entity.schema;

import java.util.HashSet;
import java.util.Set;

/**
 * 分表键
 *
 * @author Quinn
 * @desc
 * @time 2022/06/22 11:15
 */
public class ShardingKeys<K> {

    public ShardingKeys(K... keys) {
        this.keys = new HashSet<>(keys.length);
        for (K key : keys) {
            this.keys.add(key);
        }
    }

    private final Set<K> keys;

    /**
     * 分表关键字
     *
     * @return
     */
    public Set<K> getKeys() {
        return keys;
    }

}
