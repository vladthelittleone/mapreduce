package by.thelittleone.mapreduce.core.client;

import by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException;
import by.thelittleone.mapreduce.core.client.exceptions.CouldNotMapTaskException;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 0:11
 * <p/>
 * Абстрактный класс реализующий интерфейс {@link by.thelittleone.mapreduce.core.client.MapReduce}.
 * Использует паттер шаблонный метод - {@link #execute(by.thelittleone.mapreduce.core.client.MapReduce.Task)},
 * который описывает алгоритм выполнения модели распределенных вычислений. Метод map() и reduce() являются
 * основой класса и реализуют разделение задачи на подзадачи, выдача результатов вычисления соответственно. Классы,
 * наследующие интерфейс, должны реализовать два метода: отправки задачи на выполнение в
 * {@link by.thelittleone.mapreduce.core.server.AbstractExecutionPool}, количество подзадач для разбения.
 * С помощью метода executeNotMappedTask() можно задать выполнение задачи в даноом потоке,  на данной машине
 * в случае ошибки выполнения в executor.
 *
 * @author Skurishin Vladislav
 * @see by.thelittleone.mapreduce.core.server.AbstractExecutionPool
 * @see by.thelittleone.mapreduce.core.client.socket.SocketMapReducer
 * @see by.thelittleone.mapreduce.core.client.MapReduce
 */
public abstract class AbstractMapReducer implements MapReduce
{

    /**
     * Метод распределяет задачу на подзадачи, с помощью метода
     * {@link #map(by.thelittleone.mapreduce.core.client.MapReduce.Task)}.
     * Затем отправляет ее в {@link by.thelittleone.mapreduce.core.server.AbstractExecutionPool}
     * для обработки. В случае ошибки, отправляем в метод
     * {@link #handleExecutionErrors(by.thelittleone.mapreduce.core.client.MapReduce.Task, Exception)}.
     * Если результаты получен от выполнения подзадач, то сливаем их, с помощью метода
     * {@link by.thelittleone.mapreduce.core.client.MapReduce.Task#reduce(java.util.Set)}
     *
     * @param task - задача для распределения и последующего выполнения.
     * @param <T>  - тип возращавемого результата вычисления.
     * @return - возвращает результат вычисления задачи.
     * @throws CouldNotExecuteTaskException
     * @throws Exception
     * @see by.thelittleone.mapreduce.core.client.MapReduce
     * @see by.thelittleone.mapreduce.core.client.MapReduce.Task
     */
    public final <T> T execute(final Task<T> task) throws Exception
    {

        Set<Task<T>> tasks = null;
        Set<T> results = null;

        try
        {
            tasks = map(task);
            results = sendToExecutor(tasks);
            validate(results, tasks);
        }
        catch (CouldNotExecuteTaskException | CouldNotMapTaskException e)
        {
            handleExecutionErrors(task, e);
        }

        return task.reduce(results);
    }

    /**
     * Валидация резултатов вычисленния подазадач и разделения задачи на подзадачи.
     *
     * @param results - результаты вычисления подзадач.
     * @param tasks   - множество подзадач.
     * @param <T>     - тип результата вычисления.
     * @throws CouldNotExecuteTaskException
     * @throws CouldNotMapTaskException
     * @see by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException
     * @see by.thelittleone.mapreduce.core.client.exceptions.CouldNotMapTaskException
     */
    private <T> void validate(Set<T> results, Set<Task<T>> tasks) throws CouldNotExecuteTaskException, CouldNotMapTaskException
    {
        if (tasks == null || tasks.isEmpty())
        {
            throw new CouldNotMapTaskException("method map() can not return null or empty set.");
        }

        if (results == null || results.isEmpty())
        {
            throw new CouldNotExecuteTaskException("method sendToExecutor() can not return null or empty set.");
        }
    }

    /**
     * Метод обработки исключений полученных при отправке на удаленный исполнитель (executor) и
     * при разделении задачи на подзадачи. Метод может быть переопределен в наследуемых классах.
     * В данной реализации при {@link #executeNotMappedTask()}, имеющем значение {@code true} -
     * задача выполняется в вызывающем метод потоке, при значении {@code false} - выбрасывается исключение.
     *
     * @param task - объект задачи.
     * @param e    - полученное исключение.
     * @param <T>  - тип результата вычислений задачи.
     * @return результат выполнения задачи.
     * @throws CouldNotExecuteTaskException
     */
    protected <T> T handleExecutionErrors(Task<T> task, Exception e) throws CouldNotExecuteTaskException
    {
        if (executeNotMappedTask())
        {
            return task.execute();
        }
        throw new CouldNotExecuteTaskException(e);
    }

    /**
     * Метод, отвечающий за разбиение задачи на подзадачи.
     * Количество подзадач, которое нужно получить на выходе
     * определяется с помощью метода {@link #getNumberOfSubTasks()}.
     * Если метод возвращает <code>0<code/>, пробрасывается исключение
     * {@link java.lang.IllegalArgumentException}. Полученной кол-во в результате
     * используется при вызове метода {@link by.thelittleone.mapreduce.core.client.MapReduce.Task#getSubTasks(int)},
     * который совершает нужное нам разбиение в зависимости от реализации интерфейса задачи.
     *
     * @param task - реализация задачи.
     * @param <T>  - тип результата вычисления.
     * @return возвращает множество подзадач.
     * @throws java.lang.Exception
     */
    private <T> Set<Task<T>> map(final Task<T> task) throws Exception
    {
        int number = getNumberOfSubTasks();

        if (number == 0)
        {
            throw new IllegalArgumentException("Number of sub tasks can not be null");
        }

        return task.getSubTasks(number);
    }

    /**
     * Метод перехватчик, который отвечает за выполнение задачи в потоке вызова
     * метода {@link #execute(by.thelittleone.mapreduce.core.client.MapReduce.Task)}
     * в случае невозможности выполнить на удаленном исполнителе. То есть, если
     * метод возвращает true, то, при ошибке, задача выполнится в потоке вызова класса, в случае
     * false, пробросится исключение {@link by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException}
     */
    protected boolean executeNotMappedTask()
    {
        return false;
    }

    /**
     * Абстрактный метод, отвечающий за выполнение задачи на удаленном исполнителе, реализуюшем
     * {@link by.thelittleone.mapreduce.core.server.AbstractExecutionPool}. На вход
     * получает список подзадач для выполнения. Является абстрактным методом, который
     * может быть реализован в зависимости от нужд.
     *
     * @param tasks - подзадачи для выполнения.
     * @param <T>   - тип результатов вычисления
     * @return список результатов вычисления.
     * @throws CouldNotExecuteTaskException - пробрасывается в случае невозможности выполнить
     */
    protected abstract <T> Set<T> sendToExecutor(final Set<Task<T>> tasks) throws CouldNotExecuteTaskException;

    /**
     * Абстрактный метод, отвечающий за количество подзадач, которые будут получены
     * с помощью метода {@link #map(by.thelittleone.mapreduce.core.client.MapReduce.Task)}
     * для выполняемой задачи.
     *
     * @return количество подзадач.
     */
    protected abstract int getNumberOfSubTasks();
}
