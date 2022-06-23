package cn.oasissoft.core.db.entity;

import java.util.List;

/**
 * 分页列表
 *
 * @author Quinn
 * @desc
 * @time 2022/06/20 18:53
 */
public class PageList<T> {
    private final long total; // 总数据
    private final List<T> models; // 当前页对象集合

    public PageList(List<T> models, long total) {
        this.total = total;
        this.models = models;
    }



    /**
     * 根据分页大小获取总页数
     *
     * @param size
     * @return
     */
    public int getTotalPagesBy(int size) {
        if (this.total <= 0) {
            return 1;
        } else {
            return (int) Math.ceil((double) total / (double) size);
        }
    }

    /**
     * 获取当前总数量
     *
     * @return
     */
    public long getTotal() {
        return total;
    }

    /**
     *  获取当前总数量(int)
     * @return
     */
    public int getTotalByInteger() {
        return (int) total;
    }

    /**
     * 获取当前页对象集合
     *
     * @return
     */
    public List<T> getModels() {
        return models;
    }
}
