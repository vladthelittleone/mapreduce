package by.thelittleone.mapreduce.core.client.socket;

import by.thelittleone.mapreduce.core.client.socket.loader.ServerAddressLoader;

import java.net.InetSocketAddress;
import java.util.ListIterator;

/**
 * Реализация интерфейса {@link by.thelittleone.mapreduce.core.client.socket.AbstractSocketMapReducer}, которая
 * полуает список адрессов из реализации {@link by.thelittleone.mapreduce.core.client.socket.loader.ServerAddressLoader},
 * либо с помощью итератора адрессов.
 *
 * @author Skurishin Vladislav
 * @since 26.05.15
 */
public class SocketMapReducer extends AbstractSocketMapReducer
{
    // "Прыгающий" итератор адрессов.
    private HoppingIterator<InetSocketAddress> itr;

    public SocketMapReducer(ServerAddressLoader addressLoader, int numberOfSubTasks)
    {
        addressLoader.load();
        itr = new HoppingIterator<>(addressLoader.getServerAddresses());
        this.numberOfSubTasks = numberOfSubTasks;
    }

    public SocketMapReducer(ListIterator<InetSocketAddress> iterator, int numberOfSubTasks)
    {
        itr = new HoppingIterator<>(iterator);
        this.numberOfSubTasks = numberOfSubTasks;
    }

    @Override
    protected HoppingIterator<InetSocketAddress> inetSocketAddresses()
    {
        return itr;
    }
}
