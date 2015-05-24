package by.thelittleone.mapreduce.core.client.api;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 11.03.14
 * Time: 22:21
 * <p/>
 * Интерфейс отвечающий за вывод результата вычесления задачи
 * и объединение результатов вычислений подзадач в один единый результат.
 * {@link by.thelittleone.mapreduce.core.client.MapReduce.Task}.
 *
 * @param <T> - тип параметра.
 * @author Skurishin Vladislav
 * @see by.thelittleone.mapreduce.core.client.MapReduce
 * @see by.thelittleone.mapreduce.core.client.MapReduce.Task
 * @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
 */
public interface Reducible<T>
{
    /**
     * Метод, отвечающий за выполенение задачи, реализующей этот
     * интерфейс, и возвращающи результат вычисления.
     *
     * @return возвращает результат вычислений.
     */
    T execute();

    /**
     * Метод, отвечающий за объединение множества результатов выполенения
     * подзадач, в один, для основной задачи.
     *
     * @param results - множество результатов подзадач.
     * @return - возвращает результат для основной задачи.
     */
    T reduce(Set<T> results);
}
