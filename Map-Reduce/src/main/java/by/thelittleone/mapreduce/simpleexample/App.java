package by.thelittleone.mapreduce.simpleexample;

import by.thelittleone.mapreduce.core.client.MapReducer;
import by.thelittleone.mapreduce.core.client.exceptions.CouldNotExecuteTaskException;
import by.thelittleone.mapreduce.core.client.socket.SocketMapReducer;
import by.thelittleone.mapreduce.core.client.socket.loader.FileAddressLoader;

import java.io.IOException;

/**
 * Project: Map-Reduce
 * Date: 16.03.14
 * Time: 17:57
 *
 * @author Skurishin Vladislav
 */
public class App
{

    public static void main(String[] args) throws CouldNotExecuteTaskException, IOException, ClassNotFoundException
    {
        MapReducer socketMapReducer = new SocketMapReducer(new FileAddressLoader("src/main/resources/addresses.txt"), 10);
        System.out.println(socketMapReducer.execute(new EratosthenesTask(10)));
    }
}
