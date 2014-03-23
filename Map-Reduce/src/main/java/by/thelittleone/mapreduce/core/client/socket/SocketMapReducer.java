package by.thelittleone.mapreduce.core.client.socket;


import by.thelittleone.mapreduce.core.client.AbstractMapReducer;
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
 * Реализация абстрактного класса {@link by.thelittleone.mapreduce.core.client.AbstractMapReducer}.
 * Обеспечивает взаимодействие с "нодами" с помощью сокетов.
 *
 * @see by.thelittleone.mapreduce.core.client.MapReduce
 * @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
 * @author Skurishin Vladislav
 */
public class SocketMapReducer extends AbstractMapReducer
{
    // "Прыгающий" итератор адрессов.
    private HoppingIterator<InetSocketAddress> itr;

    // количество подзадач.
    private int numberOfSubTasks;

    // выполнение не отправленных.
    private boolean executingNotSanded = false;

    public SocketMapReducer(ServerAddressLoader addressLoader, Integer numberOfSubTasks)
    {
        addressLoader.load();
        itr = new HoppingIterator<>(addressLoader.getServerAddresses());
        this.numberOfSubTasks = numberOfSubTasks;
    }

    public SocketMapReducer(ListIterator<InetSocketAddress> iterator, Integer numberOfSubTasks)
    {
        itr = new HoppingIterator<>(iterator);
        this.numberOfSubTasks = numberOfSubTasks;
    }

    /**
     * Отправка подзадач на сокет сервер {@link by.thelittleone.mapreduce.core.server.SocketExecutionPool},
     * который их выполнит и вернет результат. Количество отправляемых задач - {@link #getNumberOfSubTasks()}.
     * Для каждой задачи выделяется поток {@link by.thelittleone.mapreduce.core.client.socket.SocketMapReducer.Sender},
     * который создает для него сокет и отправляет/получает подзадачи/результаты вычислений. Метод ожидает выполнения каждой
     * подазадачи и в результате возвращает множество результатов. В случае неудачи, есть возможность с помощью
     * {@link #executeNotMappedTask()} определить выполнение задачи в вызывающем потоке.
     *
     * @param tasks - подзадачи для выполнения.
     * @param <T> - тип результата вычислений.
     * @see by.thelittleone.mapreduce.core.client.socket.SocketMapReducer.Sender
     * @see by.thelittleone.mapreduce.core.server.SocketExecutionPool
     * @return - возвращает множество результатов вычислений подзадач.
     * @throws CouldNotExecuteTaskException
     */
    @Override
    protected <T> Set<T> sendToExecutor(Set<Task<T>> tasks) throws CouldNotExecuteTaskException
    {
        ExecutorService es = Executors.newFixedThreadPool(getNumberOfSubTasks());

        Set<T> results = new HashSet<>();
        Map<Task<T>, Future<T>> futures = new HashMap<>();

        for (Task<T> t : tasks) {
            Future<T> f = es.submit(new Sender<>(t));
            futures.put(t, f);
        }

        for (Map.Entry<Task<T>, Future<T>> e : futures.entrySet()) {

            // Ждем выполнения.
            Future<T> f = e.getValue();
            Task<T> task = e.getKey();

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

    /**
     * Метод, отвечающий за количество подзадач, которые будут получены
     * с помощью метода {@link #map(by.thelittleone.mapreduce.core.client.MapReduce.Task)}
     * для выполняемой задачи.
     *
     * @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
     * @see by.thelittleone.mapreduce.core.client.MapReduce.Task
     * @return количество подзадач.
     */
    @Override
    protected int getNumberOfSubTasks()
    {
        return numberOfSubTasks;
    }

    /**
     * Класс реализующий {@link java.util.concurrent.Callable},
     * который отправляет задание на сокет сервер - {@link by.thelittleone.mapreduce.core.server.SocketExecutionPool}
     * и получает от него ответ.
     *
     * @param <T> - тип результата выислений.
     */
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

    /**
     * Итератор, проходящий по коллекции аналогично "попрыгунчику".
     * @param <T> - тип элементов.
     */
    private class HoppingIterator<T>
    {
        private ListIterator<T> itr;

        private boolean direction = true;

        private HoppingIterator(ListIterator<T> itr)
        {
            this.itr = itr;
        }

        /**
         * Синхронизованный метод получения следующего элемента.
         * @return - элемент итератора.
         */
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

    public void setExecutingNotSanded(boolean executeNotSandedTasks)
    {
        this.executingNotSanded = executeNotSandedTasks;
    }

    private InetSocketAddress getNextServer()
    {
        return itr.get();
    }

    private void setNumberOfSubTasks(int numberOfSubTasks){
        this.numberOfSubTasks = numberOfSubTasks;
    }
}
