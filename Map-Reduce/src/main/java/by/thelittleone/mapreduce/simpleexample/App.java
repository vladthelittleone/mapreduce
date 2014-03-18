package by.thelittleone.mapreduce.simpleexample;

import by.thelittleone.mapreduce.core.client.AbstractMapReducer;
import by.thelittleone.mapreduce.core.client.socket.SocketMapReducer;
import by.thelittleone.mapreduce.core.client.socket.loader.FileAddressLoader;

/**
 * Project: Map-Reduce
 * Date: 16.03.14
 * Time: 17:57
 *
 * @author Skurishin Vladislav
 */
public class App
{

    public static void main(String[] args) throws Exception
    {
        AbstractMapReducer socketAbstractMapReducer = new SocketMapReducer(new FileAddressLoader("src/main/resources/addresses.txt"), 10);
        System.out.println(socketAbstractMapReducer.execute(new EratosthenesTask(10)));
    }
}
