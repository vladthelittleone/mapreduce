package client.task;

/**
 * Project: Map-Reduce
 * Date: 11.03.14
 * Time: 22:21
 *
 * @author Skurishin Vladislav
 */
public interface Reducible<T> {
    T result();
}
