package by.thelittleone.mapreduce.core.client;

import by.thelittleone.mapreduce.core.client.api.Mappable;
import by.thelittleone.mapreduce.core.client.api.Reducible;

/**
 * Project: Map-Reduce
 * Date: 19.03.14
 * Time: 1:17
 *
 * @author Skurishin Vladislav
 */
public interface MapReduce
{
    public <T> T execute(final Task<T> task) throws Exception;

    public static interface Task<T> extends Reducible<T>, Mappable<Task<T>> {}
}
