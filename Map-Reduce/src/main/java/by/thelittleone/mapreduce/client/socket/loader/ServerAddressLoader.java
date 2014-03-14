package by.thelittleone.mapreduce.client.socket.loader;


import java.net.InetSocketAddress;
import java.util.Queue;

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

    Queue<InetSocketAddress> getServerAddresses();
}
