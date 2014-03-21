package by.thelittleone.mapreduce.core.client;

import by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 0:11
 *
 * Абстрактный класс реализующий интерфейс {@link by.thelittleone.mapreduce.core.client.MapReduce}.
 * Использует паттер шаблонный метод - execute(), который описывает алгоритм выполнения модели
 * распределенных вычислений. Метод map() и reduce() являются основой класса и реализуют
 * разделение задачи на подзадачи, выдача результатов вычисления соответственно. Классы,
 * наследующие интерфейс, должны реализовать два метода: отправки задачи на выполнение в
 * {@link by.thelittleone.mapreduce.core.server.AbstractExecutionPool}, количество подзадач для разбения.
 * С помощью метода executeNotMappedTask() можно задать выполнение задачи в даноом потоке,  на данной машине
 * в случае ошибки выполнения в executor.
 *
 * @see by.thelittleone.mapreduce.core.server.AbstractExecutionPool
 * @see by.thelittleone.mapreduce.core.client.socket.SocketMapReducer
 * @see by.thelittleone.mapreduce.core.client.MapReduce
 * @author Skurishin Vladislav
 */
public abstract class AbstractMapReducer implements MapReduce
{

    public final <T> T execute(final Task<T> task) throws Exception
    {
        Set<Task<T>> tasks = map(task);
        Set<T> results = sendToExecutor(tasks);

        if (results == null || results.isEmpty()) {

            if (executeNotMappedTask()) {
                return task.execute();
            }

            throw new CouldNotExecuteTaskException("method sendToServer() return null or empty set.");
        }

        return task.reduce(results);
    }

    private <T> Set<Task<T>> map(final Task<T> task)
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

    protected abstract <T> Set<T> sendToExecutor(final Set<Task<T>> tasks) throws CouldNotExecuteTaskException;

    protected abstract int getNumberOfSubTasks();
}
