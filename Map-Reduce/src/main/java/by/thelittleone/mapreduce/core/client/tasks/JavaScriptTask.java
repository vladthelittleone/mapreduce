package by.thelittleone.mapreduce.core.client.tasks;

import by.thelittleone.mapreduce.core.client.MapReduce.ReducibleTask;
import by.thelittleone.mapreduce.core.client.api.Reducible;

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
 * TODO
 * подумать, как разделить.
 *
 * @author Skurishin Vladislav
 */
public class JavaScriptTask implements ReducibleTask<Object>
{
    private String methodName;

    private Reader reader;

    private ScriptEngine engine;

    public JavaScriptTask(String methodName, Reader reader)
    {
        ScriptEngineManager mgr = new ScriptEngineManager();

        this.engine = mgr.getEngineByName("nashorn");
        this.methodName = methodName;
        this.reader = reader;
    }

    public JavaScriptTask(String methodName, String path) throws UnsupportedEncodingException, FileNotFoundException
    {
        this.methodName = methodName;
        this.reader = load(path);
    }

    @Override
    public Object execute()
    {
        try {
            engine.eval(reader);
            return ((Invocable) engine).invokeFunction(methodName);
        }
        catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load and evaluate the specified JavaScript file.
     *
     * @param path Path to UTF-8 encoded JavaScript file.
     * @return Last evaluation result (discarded.)
     */
    private Reader load(String path) throws UnsupportedEncodingException, FileNotFoundException
    {
        FileInputStream file = new FileInputStream(path);
        return new InputStreamReader(file, "UTF-8");
    }

    @Override
    public Object reduce(Set<Object> results)
    {
        return results;
    }

    @Override
    public Set<Reducible<Object>> getSubTasks(int fragments)
    {
        Set<Reducible<Object>> set = Collections.emptySet();
        set.add(this);

        return set;
    }

    @Override
    public int parallelismLevel()
    {
        return 0;
    }
}
