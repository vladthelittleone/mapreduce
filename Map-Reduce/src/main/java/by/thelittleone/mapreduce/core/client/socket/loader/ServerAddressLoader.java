package by.thelittleone.mapreduce.core.client.socket.loader;


import java.net.InetSocketAddress;
import java.util.ListIterator;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:06
 *
 * @author Skurishin Vladislav
 */
public interface ServerAddressLoader
{
    void load();

    ListIterator<InetSocketAddress> getServerAddresses();
}
