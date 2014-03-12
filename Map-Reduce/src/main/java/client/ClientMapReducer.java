package client;

import client.loader.ServerAddressLoader;
import client.task.Mappable;
import client.task.Reducible;

import java.util.Set;


/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 0:11
 *
 * @author Skurishin Vladislav
 */
public class ClientMapReducer {
    private ServerAddressLoader loader;

    public ClientMapReducer() {
    }

    public final <T, K extends Reducible<T>> T execute(Task<T, K> task) {
        Set<K> tasks = map(task);
        return reduce(task);
    }

    private <T, K extends Reducible<T>> Set<K> map(Task<T, K> task) {
        return task.getSubTasks(10);
    }

    private <T, K extends Reducible<T>> T reduce(Task<T, K> task) {
        return task.result();
    }

    public void setLoader(ServerAddressLoader loader) {
        this.loader = loader;
    }

    private interface Task<T, K extends Reducible<T>> extends Reducible<T>, Mappable<K> {
        Set<K> getSubTasks(int fragments);

        T result();
    }

    public static interface ReducibleTask<T> extends Task<T, Reducible<T>> {}

    public static interface MultiplyTask<T> extends Task<T, MultiplyTask<T>>, Reducible<T> {}

}
