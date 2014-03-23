package by.thelittleone.mapreduce.core.server;

import by.thelittleone.mapreduce.core.client.MapReduce.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinTask;

/**
 * Project: Map-Reduce
 * Date: 19.03.14
 * Time: 0:26
 *
 * Класс - сокет сервер, отвечающий за запуск запуск полученных задач
 * c {@link by.thelittleone.mapreduce.core.client.socket.SocketMapReducer} с
 * помощью сокет соединения и возврата результатов вычислений. Является реализации
 * абстрактного класса {@link by.thelittleone.mapreduce.core.server.AbstractExecutionPool}.
 *
 * @see by.thelittleone.mapreduce.core.server.AbstractExecutionPool
 * @see by.thelittleone.mapreduce.core.client.socket.SocketMapReducer
 * @author Skurishin Vladislav
 */
public class SocketExecutionPool extends AbstractExecutionPool
{
    // Порт сокет сервера
    private int port;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public SocketExecutionPool(int port) throws Exception {
        super();
        this.port = port;
    }

    public SocketExecutionPool(int limit, int port) throws Exception
    {
        super(limit);
        this.port = port;
        startExecution();
    }

    public SocketExecutionPool(int parallelism, int limit, int port) throws Exception
    {
        super(parallelism, limit);
        this.port = port;
        startExecution();
    }

    /**
     * Метод запускает прослушивание порта на предмет сокет соединения
     * {@link by.thelittleone.mapreduce.core.client.socket.SocketMapReducer},
     * передающего задание для выполнения.
     * @throws Exception
     */
    @Override
    protected void startExecution() throws Exception
    {
        try (ServerSocket ss = new ServerSocket(port)) {

            while (!isShutdown()) {
                final Socket s = ss.accept();
                System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
                executorService.execute(new ResultSender(s));
            }
        }
        catch (IOException e) {
            throw new ConnectException(String.format("Execution while creation sever socket. %s", e.getMessage()));
        }
    }

    /**
     * Завершить работу пулла.
     */
    @Override
    public void shutdown()
    {
        super.shutdown();
        executorService.shutdown();
    }

    /**
     * Класс, реализующий {@link java.lang.Runnable}, который получает задание от
     * сокета {@link by.thelittleone.mapreduce.core.client.socket.SocketMapReducer} и
     * формирует {@link java.util.concurrent.ForkJoinTask} для выполнения и получения результата.
     */
    private class ResultSender implements Runnable
    {
        private Socket s;

        private ResultSender(Socket s)
        {
            this.s = s;
        }

        @Override
        public void run()
        {
            try {
                ObjectOutputStream objOut = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(s.getInputStream());

                Task task = (Task) objIn.readObject();

                ForkJoinTask<?> forkJoinTask = getTask(task);

                // TODO
                // ForkJoinTask.isCompletedAbnormally()

                objOut.writeObject(forkJoinTask.invoke());

                objIn.close();
                objOut.close();
            }
            catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
