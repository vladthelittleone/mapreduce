package by.thelittleone.mapreduce.core.client.api;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 11.03.14
 * Time: 22:21
 *
 * @author Skurishin Vladislav
 */
public interface Reducible<T>
{
    T execute();

    /**
     * TODO
     * Подумать, как избавиться от отправки этого метода в случае ReducibleTask.
     */
    T reduce(Set<T> results);
}
