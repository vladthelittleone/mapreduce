package by.thelittleone.mapreduce.core.client;

import by.thelittleone.mapreduce.core.client.api.Reducible;
import by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 0:11
 *
 * @author Skurishin Vladislav
 */
public abstract class AbstractMapReducer implements MapReduce
{

    public final <T, K extends Reducible<T>> T execute(final Task<T, K> task) throws Exception
    {
        Set<K> tasks = map(task);
        Set<T> results = sendToExecutor(tasks);

        if (results == null || results.isEmpty()) {

            if (executeNotMappedTask()) {
                return task.execute();
            }

            throw new CouldNotExecuteTaskException("method sendToServer() return null or empty set.");
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
        return false;
    }

    protected abstract <T, K extends Reducible<T>> Set<T> sendToExecutor(final Set<? extends K> tasks) throws CouldNotExecuteTaskException;

    protected abstract int getNumberOfSubTasks();
}
