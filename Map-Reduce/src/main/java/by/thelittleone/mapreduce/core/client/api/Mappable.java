package by.thelittleone.mapreduce.core.client.api;

import by.thelittleone.mapreduce.core.client.MapReduce.Task;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 13:24
 * <p/>
 * Интерфейс отвечающий за разделение (маппинг) задач
 * {@link by.thelittleone.mapreduce.core.client.MapReduce.Task} на подзадачи.
 * Хранит в себе константы порогового значения (limit), которое используется
 * при запуске задач на сервере с помощью {@link java.util.concurrent.ForkJoinPool}.
 *
 * @param <T> - тип разделяемой зайдачи.
 * @author Skurishin Vladislav
 * @see java.util.concurrent.ForkJoinPool
 * @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
 * @see by.thelittleone.mapreduce.core.client.MapReduce
 * @see by.thelittleone.mapreduce.core.client.MapReduce.Task
 */
public interface Mappable<T extends Task<?>> {
    // Пороговое значение
    int NO_LIMIT = 0;
    int LOW_LIMIT = 100;
    int MEDIUM_LIMIT = 500;
    int HEIGHT_LIMIT = 1000;

    /**
     * Метод разбивает задачу, реализующую этот интерфейс на подзадачи, количество
     * которых не больше параметра fragments. На выходе получаем множество подзадач,
     * которые реализуют интерфейс {@link by.thelittleone.mapreduce.core.client.MapReduce.Task}.
     *
     * @param fragments - предел количества подзадач.
     * @return множество подзадач.
     */
    Set<T> getSubTasks(int fragments);

    /**
     * Пороговое значение последовательного выполнения для фрэймворка {@link java.util.concurrent.ForkJoinPool}.
     * Является ключивым моментом наилучшего использования стратегии "разделяй и властвуй". Если данное значение будет
     * больше хранимого на сервере, то сервер попытается разделить задачу и выполнить параллельно,  а не последовательно.
     *
     * @return пороговое значение.
     * @see java.util.concurrent.ForkJoinPool
     */
    int limit();

    /**
     * Определяет может ли задача быть разделена на подзадачи или нет.
     * Стоит вызывать данный метод перед методом {@link by.thelittleone.mapreduce.core.client.api.Mappable#getSubTasks(int)}
     *
     * @return true - задача разбивается на подзадачи, false - нет.
     */
    boolean isMappable();
}
