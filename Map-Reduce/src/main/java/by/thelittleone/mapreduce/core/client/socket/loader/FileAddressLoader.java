package by.thelittleone.mapreduce.core.client.socket.loader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:08
 * Класс, реализующий интерфейс {@link by.thelittleone.mapreduce.core.client.socket.loader.ServerAddressLoader}
 * и загружающий адресса сокет сервера из файла.
 * Файл должен содержать в кажой строке шаблон: {@code ip - адресс : порт}
 *
 * @author Skurishin Vladislav
 * @see by.thelittleone.mapreduce.core.client.socket.loader.ServerAddressLoader
 */
public class FileAddressLoader implements ServerAddressLoader
{
    private final String path;

    private final IPAddressValidator validator = new IPAddressValidator();

    private List<InetSocketAddress> addresses = new LinkedList<>();

    public FileAddressLoader(String path)
    {
        this.path = path;
    }

    /**
     * Чтение файла, путь к которому указан с помощью консруктора и
     * последующий вызов методов парсящих данный файл.
     *
     * @throws java.lang.RuntimeException
     */
    @Override
    public void load()
    {
        try
        {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            ByteBuffer mBuf = ByteBuffer.wrap(bytes);

            mBuf.rewind();

            addresses = parseBuffer(mBuf);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Some troubles with generating addresses.", e);
        }
    }

    /**
     * @return Возвращает итератор адрессов серверов.
     */
    @Override
    public ListIterator<InetSocketAddress> getServerAddresses()
    {
        return addresses.listIterator();
    }

    /**
     * Метод получает из буффера ip - адресса и порты серверов.
     * Каждая строка проверяется валидатором {@link by.thelittleone.mapreduce.core.client.socket.loader.IPAddressValidator}.
     *
     * @param mBuf - буффер данных.
     * @return - возвращает список адрессов.
     * @throws NumberFormatException
     */
    private List<InetSocketAddress> parseBuffer(final ByteBuffer mBuf) throws NumberFormatException
    {

        List<InetSocketAddress> addresses = new LinkedList<>();

        StringBuilder ipBuilder = new StringBuilder();
        StringBuilder portBuilder = new StringBuilder();

        // Ссылка на билдер
        StringBuilder latch = ipBuilder;

        do
        {
            char c = (char) mBuf.get();

            // В случае конца строки переключаемся на ip - билдер
            if (c == '\n')
            {
                addAddress(addresses, ipBuilder, portBuilder);

                ipBuilder = new StringBuilder();
                portBuilder = new StringBuilder();

                latch = ipBuilder;
                continue;
            }

            // В случае двоеточия переключаемся на билдер порта
            if (c == ':')
            {
                latch = portBuilder;
                continue;
            }

            latch.append(c);

            if (!mBuf.hasRemaining())
            {
                addAddress(addresses, ipBuilder, portBuilder);
            }

        } while (mBuf.hasRemaining());

        return addresses;
    }

    /**
     * Выполняет валидацию ip, и передает {@link java.net.InetSocketAddress} в список.
     *
     * @param addresses   - список адрессов.
     * @param ipBuilder   - билдер ip.
     * @param portBuilder - билдер порта.
     * @see #parseBuffer(java.nio.ByteBuffer)
     */
    private void addAddress(List<InetSocketAddress> addresses, StringBuilder ipBuilder, StringBuilder portBuilder)
    {
        String ip = ipBuilder.toString();
        Integer port = Integer.parseInt(portBuilder.toString());

        if (!validator.validate(ip))
        {
            throw new RuntimeException(String.format("Invalid ip - address in file: %s", ip));
        }

        InetSocketAddress address = new InetSocketAddress(ip, port);
        addresses.add(address);
    }

}
