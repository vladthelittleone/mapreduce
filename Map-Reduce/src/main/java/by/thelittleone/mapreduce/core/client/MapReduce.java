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
    public <T, K extends Reducible<T>> T execute(final Task<T, K> task) throws Exception;

    public static interface Task<T, K extends Reducible<T>> extends Reducible<T>, Mappable<K> {}

    public static interface ReducibleTask<T> extends Task<T, Reducible<T>> {}

    public static interface MultiplyTask<T> extends Task<T, MultiplyTask<T>> {}

    public static interface UnreducibleTask extends Task<Boolean, Reducible<Boolean>> {}
}
