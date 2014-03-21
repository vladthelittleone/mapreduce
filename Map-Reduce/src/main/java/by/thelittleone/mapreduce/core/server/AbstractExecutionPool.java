package by.thelittleone.mapreduce.core.server;

import by.thelittleone.mapreduce.core.client.MapReduce.Task;
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
    private int limit = Task.MEDIUM_LIMIT;

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

    protected <T> T execute(final Task<T> task)
    {
        return invoke(new TaskWrapper<>(task));
    }

    protected <T> RecursiveTask<T> getTask(final Task<T> task)
    {
        return new TaskWrapper<>(task);
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

    private class TaskWrapper<T> extends RecursiveTask<T>
    {
        private Task<T> task;

        private TaskWrapper(Task<T> task)
        {
            this.task = task;
        }

        @Override
        protected T compute()
        {
            if (limit > task.limit() || !task.isMappable()) {
                return task.execute();
            }
            else {
                Set<Task<T>> subTasks = task.getSubTasks(limit);
                Set<TaskWrapper<T>> wrappers = new HashSet<>();

                for (Task<T> mt : subTasks) {
                    TaskWrapper<T> wrapper = new TaskWrapper<>(mt);
                    wrapper.fork();
                    wrappers.add(wrapper);
                }

                Set<T> results = new HashSet<>();

                for (TaskWrapper<T> tw : wrappers) {
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
