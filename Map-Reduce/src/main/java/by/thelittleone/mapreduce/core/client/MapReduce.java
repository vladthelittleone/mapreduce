package by.thelittleone.mapreduce.core.client;

import by.thelittleone.mapreduce.core.client.api.Mappable;
import by.thelittleone.mapreduce.core.client.api.Reducible;

/**
 * Project: Map-Reduce
 * Date: 19.03.14
 * Time: 1:17
 * <p/>
 * Интерфейс модели распределенных вычислений.
 * Используется для вычисления некоторых наборов распределенных задач
 * {@link by.thelittleone.mapreduce.core.client.MapReduce.Task} с использованием
 * большого количества компьютеров {@link by.thelittleone.mapreduce.core.server.AbstractExecutionPool}
 * (называемых «нодами»), образующих кластер. Работа MapReduce состоит из двух шагов: Map и Reduce.
 *
 * @author Skurishin Vladislav
 * @see by.thelittleone.mapreduce.core.server.AbstractExecutionPool
 * @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
 * @see by.thelittleone.mapreduce.core.client.MapReduce.Task
 */
public interface MapReduce
{
    /**
     * Метод распределяет задачу на части - подзадачи, отправляет на обработку.
     * После возвращает объединенный результат из результатов всех подзадач
     * того же типа, который указан в джинерике задачи.
     *
     * @param task - задача для распределения и последующего выполнения.
     * @param <T>  - тип результата вычислений.
     * @return результат вычисления
     * @throws Exception - исключения зависят от реализации модели.
     */
    public <T> T execute(final Task<T> task) throws Exception;

    /**
     * Интерфейс задачи выполняемой моделью распределенных вычислений.
     * Реализует интерфейсы {@link by.thelittleone.mapreduce.core.client.api.Mappable}
     * и интерфейс {@link by.thelittleone.mapreduce.core.client.api.Reducible}. Для использования реализаций
     * {@link by.thelittleone.mapreduce.core.client.MapReduce} достаточно создать задачу, реализующую данный интерфейс.
     *
     * @param <T> - тип результата вычислений.
     * @see by.thelittleone.mapreduce.core.client.api.Reducible
     * @see by.thelittleone.mapreduce.core.client.api.Mappable
     */
    public static interface Task<T> extends Reducible<T>, Mappable<Task<T>>
    {
    }
}
