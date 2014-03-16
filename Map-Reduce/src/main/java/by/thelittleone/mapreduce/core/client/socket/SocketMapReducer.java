package by.thelittleone.mapreduce.core.client.socket;


import by.thelittleone.mapreduce.core.client.MapReducer;
import by.thelittleone.mapreduce.core.client.api.Reducible;
import by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException;
import by.thelittleone.mapreduce.core.client.socket.loader.ServerAddressLoader;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
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
    private HoppingIterator<InetSocketAddress> itr;

    private final int numberOfSubTasks;

    private boolean executingNotSanded = false;

    public SocketMapReducer(ServerAddressLoader addressLoader, Integer numberOfSubTasks)
    {
        addressLoader.load();
        itr = new HoppingIterator<>(addressLoader.getServerAddresses());
        this.numberOfSubTasks = numberOfSubTasks;
    }

    @Override
    protected int getNumberOfSubTasks()
    {
        return numberOfSubTasks;
    }

    @Override
    protected <T, K extends Reducible<T>> Set<T> sendToExecutor(Set<? extends K> tasks) throws CouldNotExecuteTaskException
    {
        ExecutorService es = Executors.newFixedThreadPool(getNumberOfSubTasks());

        Set<T> results = new HashSet<>();
        Map<K, Future<T>> futures = new HashMap<>();

        for (K t : tasks) {
            Future<T> f = es.submit(new Sender<>(t));
            futures.put(t, f);
        }

        for (Map.Entry<K, Future<T>> e : futures.entrySet()) {

            Future<T> f = e.getValue();
            K task = e.getKey();

            try {
                results.add(f.get());
            }
            catch (InterruptedException | ExecutionException ex) {

                if (!executingNotSanded) {
                    throw new CouldNotExecuteTaskException("Could not execute sub task on server.", ex);
                }

                results.add(task.execute());
            }

        }

        es.shutdown();
        return results;
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
            InetSocketAddress address = getNextServer();
            InetAddress addr = address.getAddress();

            T result;

            try (Socket s = new Socket(addr, address.getPort())) {
                if (!s.isConnected()) {
                    //сюда мы попадаем, если соединение не установилось
                    throw new ConnectException("Could not connect to server.");
                }

                ObjectOutputStream objOut = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(s.getInputStream());

                objOut.writeObject(task);

                result = (T) objIn.readObject();

                objIn.close();
                objOut.close();
            }

            return result;
        }
    }


    private class HoppingIterator<T>
    {
        private ListIterator<T> itr;

        private boolean direction = true;

        private HoppingIterator(ListIterator<T> itr)
        {
            this.itr = itr;
        }

        public synchronized T get()
        {

            if (!itr.hasNext()) {
                direction = false;
            }

            if (!itr.hasPrevious()) {
                direction = true;
            }

            return direction ? itr.next() : itr.previous();
        }
    }

    private InetSocketAddress getNextServer()
    {
        return itr.get();
    }

    public void setExecutingNotSanded(boolean executeNotSandedTasks)
    {
        this.executingNotSanded = executeNotSandedTasks;
    }
}
