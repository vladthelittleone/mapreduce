package client.loader;

import java.util.Map;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:06
 *
 * @author Skurishin Vladislav
 */
public interface ServerAddressLoader {
    Map<String, Long> getServerAddresses();
}
