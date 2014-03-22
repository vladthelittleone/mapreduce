package by.thelittleone.mapreduce.core.client.exceptions;

/**
 * Project: Map-Reduce
 * Date: 16.03.14
 * Time: 16:15
 * <p/>
 * Исключение, получаемое при невозможности выполнения задачи на удаленном исполнителе.
 *
 * @author Skurishin Vladislav
 */
public class CouldNotExecuteTaskException extends Exception {
    public CouldNotExecuteTaskException() {
        super();
    }

    public CouldNotExecuteTaskException(String message) {
        super(message);
    }

    public CouldNotExecuteTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotExecuteTaskException(Throwable cause) {
        super(cause);
    }

    protected CouldNotExecuteTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
