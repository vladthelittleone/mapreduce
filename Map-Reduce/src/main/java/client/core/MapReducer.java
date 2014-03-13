package client.core;

import client.core.task.Mappable;
import client.core.task.Reducible;

import java.util.HashSet;
import java.util.Set;


/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 0:11
 *
 * @author Skurishin Vladislav
 */
public abstract class MapReducer {
    public MapReducer() {
    }

    public final <T, K extends Reducible<T>> T execute(Task<T, K> task) {
        Set<K> tasks = map(task);
        Set<T> results = new HashSet<>();

        for (K t : tasks) {
            results.add(sendToExecutor(t));
        }

        return task.reduce(results);
    }

    private <T, K extends Reducible<T>> Set<K> map(Task<T, K> task) {
        int number = getExecutorsNumber();
        return task.getSubTasks(number);
    }

    protected abstract int getExecutorsNumber();

    protected abstract <T> T sendToExecutor(Reducible<T> task);

    private interface Task<T, K extends Reducible<T>> extends Reducible<T>, Mappable<K> {}

    public static interface ReducibleTask<T> extends Task<T, Reducible<T>> {}

    public static interface MultiplyTask<T> extends Task<T, MultiplyTask<T>>, Reducible<T> {}
}
