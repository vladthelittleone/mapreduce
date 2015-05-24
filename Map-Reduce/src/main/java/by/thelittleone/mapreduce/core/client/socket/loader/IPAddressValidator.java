package by.thelittleone.mapreduce.core.client.socket.loader;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 2:36
 * <p/>
 * Класс валидации ip - адресса.
 *
 * @author Skurishin Vladislav
 */

public class IPAddressValidator
{
    private Pattern pattern;

    private static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public IPAddressValidator()
    {
        pattern = Pattern.compile(IP_ADDRESS_PATTERN);
    }

    /**
     * Валидация ip с помощью регулярного выражения.
     *
     * @param ip ip - адресс для валидации.
     * @return true - валидный ip, false - не валидный ip
     */
    public boolean validate(final String ip)
    {
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
