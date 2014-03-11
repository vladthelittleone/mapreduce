package client.task;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 13:24
 *
 * @author Skurishin Vladislav
 */
public interface Mappable<T extends Reducible> {
    Set<T> getSubTasks(int fragments);
}
