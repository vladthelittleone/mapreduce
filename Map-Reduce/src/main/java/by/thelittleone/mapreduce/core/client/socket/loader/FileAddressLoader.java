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
 *
 * @author Skurishin Vladislav
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

    @Override
    public void load()
    {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            ByteBuffer mBuf = ByteBuffer.wrap(bytes);

            mBuf.rewind();

            addresses = parseBuffer(mBuf);
        }
        catch (IOException e) {
            throw new RuntimeException("Some troubles with generating addresses.", e);
        }
    }

    @Override
    public ListIterator<InetSocketAddress> getServerAddresses()
    {
        return addresses.listIterator();
    }

    private List<InetSocketAddress> parseBuffer(final ByteBuffer mBuf) throws NumberFormatException
    {

        List<InetSocketAddress> addresses = new LinkedList<>();

        StringBuilder ipBuilder = new StringBuilder();
        StringBuilder portBuilder = new StringBuilder();

        StringBuilder latch = ipBuilder;

        do {
            char c = (char) mBuf.get();

            if (c == '\n') {
                addAddress(addresses, ipBuilder, portBuilder);

                ipBuilder = new StringBuilder();
                portBuilder = new StringBuilder();

                latch = ipBuilder;
                continue;
            }

            if (c == ':') {
                latch = portBuilder;
                continue;
            }

            latch.append(c);

            if (!mBuf.hasRemaining()) {
                addAddress(addresses, ipBuilder, portBuilder);
            }

        } while (mBuf.hasRemaining());

        return addresses;
    }

    private void addAddress(List<InetSocketAddress> addresses, StringBuilder ipBuilder, StringBuilder portBuilder)
    {
        String ip = ipBuilder.toString();
        Integer port = Integer.parseInt(portBuilder.toString());

        if (!validator.validate(ip)) {
            throw new RuntimeException(String.format("Invalid ip - address in file: %s", ip));
        }

        InetSocketAddress address = new InetSocketAddress(ip, port);
        addresses.add(address);
    }

}
