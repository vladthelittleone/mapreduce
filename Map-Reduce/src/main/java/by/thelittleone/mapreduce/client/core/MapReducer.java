package by.thelittleone.mapreduce.client.core;

import by.thelittleone.mapreduce.client.core.api.Mappable;
import by.thelittleone.mapreduce.client.core.api.Reducible;

import java.util.HashSet;
import java.util.Set;


/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 0:11
 *
 * @author Skurishin Vladislav
 */
public abstract class MapReducer
{
    public MapReducer()
    {
    }

    public final <T, K extends Reducible<T>> T execute(final Task<T, K> task)
    {
        Set<K> tasks = map(task);
        Set<T> results = new HashSet<>();

        results.addAll(sendToExecutor(tasks));

        if (executeTaskInCurrentThread()) {
            return task.execute();
        }

        return task.reduce(results);
    }

    private <T, K extends Reducible<T>> Set<K> map(final Task<T, K> task)
    {
        int number = getNumberOfSubTasks();

        if (number == 0) {
            throw new IllegalArgumentException("Number of sub tasks can not be null");
        }

        return task.getSubTasks(number);
    }

    protected boolean executeTaskInCurrentThread()
    {
        return false;
    }

    protected abstract int getNumberOfSubTasks();

    protected abstract <T, K extends Reducible<T>> Set<T> sendToExecutor(final Set<? extends K> tasks);

    private interface Task<T, K extends Reducible<T>> extends Reducible<T>, Mappable<K> {}

    public static interface ReducibleTask<T> extends Task<T, Reducible<T>> {}

    public static interface MultiplyTask<T> extends Task<T, MultiplyTask<T>> {}

    //    TODO
    //    public static interface UnreducibleTask<T> extends Task<T, Reducible<T>> {}
}
