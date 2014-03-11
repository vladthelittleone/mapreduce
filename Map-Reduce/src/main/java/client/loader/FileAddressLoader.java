package client.loader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:08
 *
 * @author Skurishin Vladislav
 */
public class FileAddressLoader implements ServerAddressLoader {
    // TODO: через properties.
    private final String PATH = "src/main/resources/addresses.txt";
    private final IPAddressValidator validator = new IPAddressValidator();

    @Override
    public Map<String, Long> getServerAddresses() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PATH));
            ByteBuffer mBuf = ByteBuffer.wrap(bytes);

            mBuf.rewind();

            return parseBuffer(mBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, Long> parseBuffer(final ByteBuffer mBuf) throws NumberFormatException {
        Map<String, Long> addresses = new HashMap<>();

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

    private void addAddress(Map<String, Long> addresses, StringBuilder ipBuilder, StringBuilder portBuilder) {
        String ip = ipBuilder.toString();
        Long port = Long.parseLong(portBuilder.toString());

        if (!validator.validate(ip)) {
            throw new RuntimeException(String.format("Invalid ip - address in file: %s", ip));
        }

        addresses.put(ip, port);
        System.out.println(String.format("Load address %s:%s", ip, port));
    }

}
