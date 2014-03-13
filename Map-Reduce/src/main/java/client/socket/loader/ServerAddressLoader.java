package client.socket.loader;

import java.util.Map;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:06
 *
 * @author Skurishin Vladislav
 */
public interface ServerAddressLoader {
    void load();

    int getNumberOfServers();

    Map<String, Long> getServerAddresses();
}
