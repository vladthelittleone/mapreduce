package by.thelittleone.mapreduce.simpleexample;

import by.thelittleone.mapreduce.core.client.MapReducer;
import by.thelittleone.mapreduce.core.client.api.Reducible;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 13:33
 *
 * @author Skurishin Vladislav
 */
public class EratosthenesTask implements MapReducer.ReducibleTask<Set<Integer>>, Serializable
{
    private int start;
    private int end;

    public EratosthenesTask(int end)
    {
        this.start = 1;
        this.end = end;
    }

    private EratosthenesTask(int start, int end)
    {
        this.start = start;
        this.end = end;
    }

    @Override
    public Set<Reducible<Set<Integer>>> getSubTasks(int executorsNumber)
    {
        Set<Reducible<Set<Integer>>> tasks = new HashSet<>(executorsNumber);

        if (executorsNumber == 0) {
            throw new IllegalStateException("Number of fragmentsNumber (servers) can not be zero.");
        }

        // при диапозоне равном нулю, возращаем одну задачу.
        if (start == end) {
            tasks.add(new EratosthenesTask(0, end));
            return tasks;
        }

        int range = (end - start) + 1;
        int step = 1;
        int rest = 0;

        // Если число фрагментов (серверов) больше
        // диапозона, приравниваем их
        if (range < executorsNumber) {
            executorsNumber = range;
        }
        else {
            // Кол-во равномерных распределнных задач на фрагмент (сервер)
            step = range / executorsNumber;
            // Остаток задач
            rest = range % executorsNumber;
        }

        int s = end;

        for (int i = 1; i <= executorsNumber; i++) {

            int rStep = step - 1;

            if (rest != 0) {
                rStep++;
                // уменьшаем остаток задач
                rest--;
            }

            tasks.add(new EratosthenesTask(s - rStep, s));
            s -= (rStep + 1);
        }

        return tasks;
    }

    @Override
    public Set<Integer> execute()
    {
        return primes(start, end);
    }

    @Override
    public Set<Integer> reduce(Set<Set<Integer>> results)
    {
        Set<Integer> reduce = new HashSet<>();

        for (Set<Integer> result : results) {
            reduce.addAll(result);
        }

        return reduce;
    }

    private Set<Integer> primes(int start, int end)
    {
        Set<Integer> l = new HashSet<>();

        for (int i = start; i <= end; i++) {
            if (prime(i)) {
                l.add(i);
            }
        }

        return l;
    }

    private boolean prime(int n)
    {
        for (int i = 2; i <= Math.sqrt(n); i++)
            if (n % i == 0)
                return false;
        return true;
    }
}
