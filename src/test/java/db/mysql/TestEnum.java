package db.mysql;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:30
 */
public enum TestEnum {

    A, B, C, D, E;

    public static TestEnum of(int i) {
        int count = i % 5;
        if (count == 0) {
            return A;
        } else if (count == 1) {
            return B;
        } else if (count == 2) {
            return C;
        } else if (count == 3) {
            return D;
        } else {
            return E;
        }
    }
}
