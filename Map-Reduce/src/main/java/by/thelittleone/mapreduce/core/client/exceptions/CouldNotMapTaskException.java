package by.thelittleone.mapreduce.core.client.exceptions;

/**
 * service: Map-Reduce
 * <p/>
 * Исключение, получаемое при невозможности разбиения задачи на подзадачи.
 *
 * @author Vlad Skurishin
 * @date 22.03.14.
 */
public class CouldNotMapTaskException extends Exception
{

    public CouldNotMapTaskException()
    {
        super();
    }

    public CouldNotMapTaskException(String message)
    {
        super(message);
    }

    public CouldNotMapTaskException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CouldNotMapTaskException(Throwable cause)
    {
        super(cause);
    }

    protected CouldNotMapTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
