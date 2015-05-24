package by.thelittleone.mapreduce.core.client.tasks;

import by.thelittleone.mapreduce.core.client.MapReduce.Task;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.Collections;
import java.util.Set;

/**
* Project: Map-Reduce
* Date: 19.03.14
* Time: 2:40
* <p>
* Нужно использовать jdk8!
* Задача выполняемая на JavaScript с помощью Nashorn.
* Неразделяемая на части.
*
* @see by.thelittleone.mapreduce.core.client.MapReduce.Task 
* @see by.thelittleone.mapreduce.core.client.MapReduce
* @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
* @author Skurishin Vladislav
*/
public class JavaScriptTask implements Task<Object>
{
    private String initMethod;

    private Reader reader;

    private ScriptEngine engine;

    public JavaScriptTask(String initMethod, Reader reader)
    {
        ScriptEngineManager mgr = new ScriptEngineManager();

        this.engine = mgr.getEngineByName("nashorn");
        this.initMethod = initMethod;
        this.reader = reader;
    }

    public JavaScriptTask(String initMethod, String path) throws UnsupportedEncodingException, FileNotFoundException
    {
        this.initMethod = initMethod;
        this.reader = load(path);
    }

    /**
     * Выполняет Java Script метод {@link #initMethod}.
     *
     * @return возврашает результат выполнения задачи.
     */
    @Override
    public Object execute()
    {
        try
        {
            engine.eval(reader);
            return ((Invocable) engine).invokeFunction(initMethod);
        }
        catch (ScriptException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Загрузка JavaScript файла с кодировкой UTF-8.
     *
     * @param path путь к файлу.
     * @return объект класса {@link java.io.Reader}
     */
    private Reader load(String path) throws UnsupportedEncodingException, FileNotFoundException
    {
        FileInputStream file = new FileInputStream(path);
        return new InputStreamReader(file, "UTF-8");
    }

    /**
     * @param results возвращает результат выполнения задачи.
     */
    @Override
    public Object reduce(Set<Object> results)
    {
        return results;
    }

    /**
     * @param fragments - предел количества подзадач.
     * @return возвращаем пустую коллекцию.
     * @see by.thelittleone.mapreduce.core.client.MapReduce
     * @see by.thelittleone.mapreduce.core.client.AbstractMapReducer
     */
    @Override
    public Set<Task<Object>> getSubTasks(int fragments)
    {
        return Collections.emptySet();
    }

    /**
     * @return возвращаем 0, так как задача не делится.
     * @see java.util.concurrent.ForkJoinPool
     * @see by.thelittleone.mapreduce.core.server.AbstractExecutionPool
     */
    @Override
    public int limit()
    {
        return 0;
    }

    @Override
    public boolean isMappable()
    {
        return false;
    }
}
