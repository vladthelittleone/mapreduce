package by.thelittleone.mapreduce.simpleexample;

import by.thelittleone.mapreduce.core.server.AbstractExecutionPool;
import by.thelittleone.mapreduce.core.server.SocketExecutionPool;

/**
 * Project: Map-Reduce
 * Date: 14.03.14
 * Time: 2:36
 *
 * @author Skurishin Vladislav
 */
public class ServerExample
{
    public static void main(String[] args) throws Exception
    {
        AbstractExecutionPool pool = new SocketExecutionPool(500, 6666);
    }
}
