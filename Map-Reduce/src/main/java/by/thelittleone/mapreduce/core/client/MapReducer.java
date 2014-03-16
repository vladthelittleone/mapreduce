package by.thelittleone.mapreduce.core.client;

import by.thelittleone.mapreduce.core.client.api.Mappable;
import by.thelittleone.mapreduce.core.client.api.Reducible;
import by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException;

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
    public MapReducer() {}

    public final <T, K extends Reducible<T>> T execute(final Task<T, K> task) throws CouldNotExecuteTaskException
    {
        Set<K> tasks = map(task);
        Set<T> results = new HashSet<>();

        Set<T> s = sendToExecutor(tasks);

        if (s == null || s.isEmpty()) {

            if (executeNotMappedTask()) {
                return task.execute();
            }

            throw new CouldNotExecuteTaskException("method sendToServer() return null or empty set.");

        }
        else {
            results.addAll(s);
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

    protected boolean executeNotMappedTask()
    {
        return true;
    }

    protected abstract int getNumberOfSubTasks();

    protected abstract <T, K extends Reducible<T>> Set<T> sendToExecutor(final Set<? extends K> tasks) throws CouldNotExecuteTaskException;

    private interface Task<T, K extends Reducible<T>> extends Reducible<T>, Mappable<K> {}

    public static interface ReducibleTask<T> extends Task<T, Reducible<T>> {}

    public static interface MultiplyTask<T> extends Task<T, MultiplyTask<T>> {}

    //    TODO
    //    public static interface UnreducibleTask<T> extends Task<T, Reducible<T>> {}
}
