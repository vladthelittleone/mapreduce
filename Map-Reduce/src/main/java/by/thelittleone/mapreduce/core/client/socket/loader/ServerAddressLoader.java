package by.thelittleone.mapreduce.core.client.socket.loader;


import java.net.InetSocketAddress;
import java.util.ListIterator;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:06
 * <p/>
 * Интерфейс, отвечающий за загрузку адрессов удаленных сокет серверов
 * в {@link by.thelittleone.mapreduce.core.client.socket.AbstractSocketMapReducer}.
 *
 * @author Skurishin Vladislav
 * @see by.thelittleone.mapreduce.core.client.socket.AbstractSocketMapReducer
 * @see by.thelittleone.mapreduce.core.client.socket.loader.FileAddressLoader
 */
public interface ServerAddressLoader
{
    /**
     * Метод выполняет загрузку адрессов, каким образом
     * зависит от реализации.
     */
    void load();

    /**
     * Возвращает итератор с элементами {@link java.net.InetSocketAddress}, который содержит
     * адресс сокет сервера.
     */
    ListIterator<InetSocketAddress> getServerAddresses();
}
