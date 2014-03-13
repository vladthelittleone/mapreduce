package client.socket;


import client.core.MapReducer;
import client.core.task.Reducible;
import client.socket.loader.ServerAddressLoader;

/**
 * Project: Map-Reduce
 * Date: 13.03.14
 * Time: 18:43
 *
 * @author Skurishin Vladislav
 */
public class SocketMapReducer extends MapReducer {
    private ServerAddressLoader addressLoader;

    public SocketMapReducer() {
    }

    public SocketMapReducer(ServerAddressLoader addressLoader) {
        this.addressLoader = addressLoader;
        addressLoader.load();
    }

    @Override
    protected int getExecutorsNumber() {
        return addressLoader.getNumberOfServers();
    }

    @Override
    protected <T> T sendToExecutor(Reducible<T> task) {
        return task.execute();
    }

    public void setAddressLoader(ServerAddressLoader addressLoader) {
        this.addressLoader = addressLoader;
    }
}
