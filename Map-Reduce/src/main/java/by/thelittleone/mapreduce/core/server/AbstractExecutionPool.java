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
 * Класс, отвечающий за запуск запуск полученных задач c {@link by.thelittleone.mapreduce.core.client.AbstractMapReducer}.
 * Наследуется от {@link java.util.concurrent.ForkJoinPool} в результате чего является пуллом, в котором можно запускать
 * параллельные вычислительные процессы - {@link java.util.concurrent.ForkJoinTask} и так же использовать другие функции
 * данного фрэймворка.
 *
 * @see by.thelittleone.mapreduce.core.server.AbstractExecutionPool
 * @see by.thelittleone.mapreduce.core.client.MapReduce.Task
 * @see java.util.concurrent.ForkJoinPool - советую почитать внимательней.
 * @author Skurishin Vladislav
 */
public abstract class AbstractExecutionPool extends ForkJoinPool
{
    // Пороговое значение последоватьельного выполнения
    private int limit = Task.MEDIUM_LIMIT;

    public AbstractExecutionPool() throws Exception {}

    public AbstractExecutionPool(int limit) throws Exception
    {
        this.limit = limit;
    }

    public AbstractExecutionPool(int parallelism, int limit) throws Exception
    {
        super(parallelism);
        this.limit = limit;
    }

    /**
     * Абстрактный метод, запускающий выполнение запросов от
     * {@link by.thelittleone.mapreduce.core.client.AbstractMapReducer}.
     * @throws Exception
     */
    protected abstract void startExecution() throws Exception;

    /**
     * Выполнение задачи в пулле.
     *
     * @see java.util.concurrent.ForkJoinPool#invoke(java.util.concurrent.ForkJoinTask)
     * @param task - задача для выполнения.
     * @param <T> - тип возвращаемого результата выполнения.
     * @return - результат выполнения задачи.
     */
    protected <T> T execute(final Task<T> task)
    {
        return invoke(new TaskWrapper<>(task));
    }

    /**
     * Аналогично {@link #execute(by.thelittleone.mapreduce.core.client.MapReduce.Task)} только
     * вместо выполнения, возвращает сам {@link java.util.concurrent.ForkJoinTask}.
     *
     * @param task - задача для выполнения.
     * @param <T> - тип возвращаемого результата выполнения.
     * @return - {@link java.util.concurrent.RecursiveTask} для выполнения.
     */
    protected <T> RecursiveTask<T> getTask(final Task<T> task)
    {
        return new TaskWrapper<>(task);
    }

    /**
     * Обертка вокруг класса {@link Task} для выполнения в {@link java.util.concurrent.ForkJoinPool}.
     *
     * @see java.util.concurrent.ForkJoinPool
     * @see java.util.concurrent.ForkJoinTask
     * @param <T> - тип возвращаемого результата.
     */
    private class TaskWrapper<T> extends RecursiveTask<T>
    {
        // задача для выполнения
        private Task<T> task;

        private TaskWrapper(Task<T> task)
        {
            this.task = task;
        }

        @Override
        protected T compute() {
            // Проверяем делимость задачи и пороговое значение.
            if (limit > task.limit() || !task.isMappable()) {
                return task.execute();
            }
            else {
                Set<Task<T>> subTasks = null;

                try {
                    // делим на подзадачи для параллельного вычисления с помощью ForkJoinPool.
                    subTasks = task.getSubTasks(limit);
                } catch (Exception e) {
                    // выполняем в случае ошибки без деления.
                    return task.execute();
                }

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

                // сливаем полученные результаты.
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
