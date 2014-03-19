package by.thelittleone.mapreduce.core.client.api;

import by.thelittleone.mapreduce.core.client.MapReduce.Task;

import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 13:24
 *
 * @author Skurishin Vladislav
 */
public interface Mappable<T extends Task<?>>
{
    // TODO добавить enum Level;

    Set<T> getSubTasks(int fragments);

    int parallelismLevel();

    boolean isMappable();
}
