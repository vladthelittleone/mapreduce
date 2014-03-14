package by.thelittleone.mapreduce.client.socket;


import by.thelittleone.mapreduce.client.core.MapReducer;
import by.thelittleone.mapreduce.client.core.api.Reducible;
import by.thelittleone.mapreduce.client.simpletask.EratosthenesTask;
import by.thelittleone.mapreduce.client.socket.loader.FileAddressLoader;
import by.thelittleone.mapreduce.client.socket.loader.ServerAddressLoader;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Project: Map-Reduce
 * Date: 13.03.14
 * Time: 18:43
 *
 * @author Skurishin Vladislav
 */
public class SocketMapReducer extends MapReducer
{
    private BlockingQueue<InetSocketAddress> addresses;
    private boolean inCurrentThread = false;

    public SocketMapReducer(ServerAddressLoader addressLoader)
    {
        addressLoader.load();
        addresses = new LinkedBlockingQueue<>(addressLoader.getServerAddresses());
    }

    @Override
    protected int getNumberOfSubTasks()
    {
        // Thread safe
        return addresses.size();
    }

    @Override
    protected <T, K extends Reducible<T>> Set<T> sendToExecutor(Set<? extends K> tasks)
    {
        ExecutorService es = Executors.newFixedThreadPool(getNumberOfSubTasks());
        Set<T> results = new HashSet<>();
        Set<Future<T>> futures = new HashSet<>();

        for (K t : tasks) {
            Future<T> f = es.submit(new Sender<>(t));
            futures.add(f);
        }

        for (Future<T> f : futures) {
            try {
                results.add(f.get());
            }
            catch (InterruptedException | ExecutionException e) {
                System.err.println("Could not execute task on servers");
                inCurrentThread = true;
            }
        }

        es.shutdown();

        return results;
    }

    @Override
    protected boolean executeTaskInCurrentThread()
    {
        return inCurrentThread;
    }

    private class Sender<T> implements Callable<T>
    {
        Reducible<T> task;

        private Sender(Reducible<T> task)
        {
            this.task = task;
        }

        @Override
        public T call() throws Exception
        {
            InetSocketAddress address = getAvailableServer();
            InetAddress addr = address.getAddress();

            T result;

            try (Socket s = new Socket(addr, address.getPort())) {
                if (!s.isConnected()) {
                    //сюда мы попадаем, если соединение не установилось
                    throw new RuntimeException("Could not connect to server.");
                }

                ObjectOutputStream objOut = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(s.getInputStream());

                objOut.writeObject(task);

                result = (T) objIn.readObject();

                objIn.close();
                objOut.close();
            }

            // Thread safe
            addresses.put(address);

            return result;
        }
    }


    public InetSocketAddress getAvailableServer() throws InterruptedException
    {
        // Thread safe
        return addresses.take();
    }

    public static void main(String[] args)
    {
        MapReducer socketMapReducer = new SocketMapReducer(new FileAddressLoader());
        System.out.println(socketMapReducer.execute(new EratosthenesTask(2)));
    }
}
