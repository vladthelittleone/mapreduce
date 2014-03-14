package by.thelittleone.mapreduce.client.socket.loader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:08
 *
 * @author Skurishin Vladislav
 */
public class FileAddressLoader implements ServerAddressLoader
{
    // TODO: через properties.
    private final String PATH = "src/main/resources/addresses.txt";

    private final IPAddressValidator validator = new IPAddressValidator();
    private Queue<InetSocketAddress> addresses = new LinkedList<>();

    @Override
    public void load()
    {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PATH));
            ByteBuffer mBuf = ByteBuffer.wrap(bytes);

            mBuf.rewind();

            addresses = parseBuffer(mBuf);
        }
        catch (IOException e) {
            System.err.println(String.format("Some troubles with generating addresses: %s", e.getMessage()));
        }
    }

    @Override
    public Queue<InetSocketAddress> getServerAddresses()
    {
        return addresses;
    }

    private Queue<InetSocketAddress> parseBuffer(final ByteBuffer mBuf) throws NumberFormatException
    {

        Queue<InetSocketAddress> addresses = new LinkedList<>();

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

    private void addAddress(Queue<InetSocketAddress> addresses, StringBuilder ipBuilder, StringBuilder portBuilder)
    {
        String ip = ipBuilder.toString();
        Integer port = Integer.parseInt(portBuilder.toString());

        if (!validator.validate(ip)) {
            throw new RuntimeException(String.format("Invalid ip - address in file: %s", ip));
        }

        InetSocketAddress address = new InetSocketAddress(ip, port);
        addresses.add(address);

        System.out.println(String.format("Load address %s:%s", ip, port));
    }

}
