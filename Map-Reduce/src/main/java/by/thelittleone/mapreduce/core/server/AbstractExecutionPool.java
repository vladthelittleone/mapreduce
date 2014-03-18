package by.thelittleone.mapreduce.core.server;

import by.thelittleone.mapreduce.core.client.MapReduce.MultiplyTask;
import by.thelittleone.mapreduce.core.client.api.Reducible;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Project: Map-Reduce
 * Date: 18.03.14
 * Time: 13:14
 *
 * @author Skurishin Vladislav
 */
public abstract class AbstractExecutionPool extends ForkJoinPool
{
    // Пороговое значение последоватьельного выполнения
    private int limit = 10;

    public AbstractExecutionPool(int limit) throws Exception
    {
        this.limit = limit;
    }

    public AbstractExecutionPool(int parallelism, int limit) throws Exception
    {
        super(parallelism);
        this.limit = limit;
    }

    protected abstract void startExecution() throws Exception;

    protected <T> T execute(final MultiplyTask<T> task)
    {
        return invoke(new MultiplyTaskWrapper<>(task));
    }

    protected <T> T execute(final Reducible<T> task)
    {
        RecursiveTask<T> recursiveTask = new RecursiveTask<T>()
        {
            @Override
            protected T compute()
            {
                return task.execute();
            }
        };

        return invoke(recursiveTask);
    }

    protected <T> RecursiveTask<T> getTask(final MultiplyTask<T> task)
    {
        return new MultiplyTaskWrapper<>(task);
    }

    protected <T> RecursiveTask<T> getTask(final Reducible<T> task)
    {
        return new RecursiveTask<T>()
        {
            @Override
            protected T compute()
            {
                return task.execute();
            }
        };
    }

    private class MultiplyTaskWrapper<T> extends RecursiveTask<T>
    {
        private MultiplyTask<T> task;

        private MultiplyTaskWrapper(MultiplyTask<T> task)
        {
            this.task = task;
        }

        @Override
        protected T compute()
        {
            if (limit > task.parallelismLevel()) {
                return task.execute();
            }
            else {
                Set<MultiplyTask<T>> subTasks = task.getSubTasks(limit);
                Set<MultiplyTaskWrapper<T>> wrappers = new HashSet<>();

                for (MultiplyTask<T> mt : subTasks) {
                    MultiplyTaskWrapper<T> wrapper = new MultiplyTaskWrapper<>(mt);
                    wrapper.fork();
                    wrappers.add(wrapper);
                }

                Set<T> results = new HashSet<>();

                for (MultiplyTaskWrapper<T> tw : wrappers) {
                    results.add(tw.join());
                }

                return task.reduce(results);
            }
        }
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }
}
