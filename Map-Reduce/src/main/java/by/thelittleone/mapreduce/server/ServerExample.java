package by.thelittleone.mapreduce.server;

import by.thelittleone.mapreduce.client.core.api.Reducible;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Project: Map-Reduce
 * Date: 14.03.14
 * Time: 2:36
 *
 * @author Skurishin Vladislav
 */
public class ServerExample
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        int port = 6666; // случайный порт (может быть любое число от 1025 до 65535)

        try (ServerSocket ss = new ServerSocket(port)) {

            Socket socket = ss.accept();
            System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
            System.out.println();

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            ObjectOutputStream objOut = new ObjectOutputStream(sout);
            ObjectInputStream objIn = new ObjectInputStream(sin);

            Reducible reducible = (Reducible) objIn.readObject();
            objOut.writeObject(reducible.execute());

            objIn.close();
            objOut.close();
        }
    }
}
