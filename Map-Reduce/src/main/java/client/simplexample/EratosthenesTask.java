package client.simplexample;

import client.ClientMapReducer;
import client.loader.FileAddressLoader;
import client.task.Reducible;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 13:33
 *
 * @author Skurishin Vladislav
 */
public class EratosthenesTask implements ClientMapReducer.ReducibleTask<Set<Integer>> {
    private int start;
    private int end;

    public EratosthenesTask(int end) {
        this.start = 1;
        this.end = end;
    }

    private EratosthenesTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    // TODO
    // переделать реализацию полностью в будущем. Пока сойдет так.
    // так же не забывать что это ReducibleTask
    // Integer[] - не канает
    public Set<Reducible<Set<Integer>>> getSubTasks(int fragments) {
        Set<Reducible<Set<Integer>>> tasks = new HashSet<>(fragments);

        // TODO
        // Validate fragments = 0;

        if (start == end) {
            tasks.add(new EratosthenesTask(start, end));
            return tasks;
        }

        int range = (end - start) + 1;
        int step = 1;
        int rest = 0;

        if (range < fragments) {
            fragments = range;
        } else {
            step = range / fragments;
            rest = range % fragments;
        }

        int s = end;

        for (int i = 1; i < fragments; i++) {

            int rStep = step;

            if (rest != 0) {
                rStep++;
                rest--;
            }

            tasks.add(new EratosthenesTask(s - rStep, s));

            s -= rStep;
        }

        return tasks;
    }

    @Override
    public Set<Integer> result() {
        return primes(start, end);
    }

    // TODO
    // Так же переписать.
    private Set<Integer> primes(int start, int end) {
        Set<Integer> l = new HashSet<>();
        for (int i = start; i <= end; i++) {
            boolean isPrime = true;

            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    isPrime = false;
                    break;
                }
            }

            if (isPrime) {
                l.add(i);
            }
        }
        return l;
    }

    public static void main(String[] args) {
        ClientMapReducer clientMapReducer = new ClientMapReducer();
        clientMapReducer.setLoader(new FileAddressLoader());
        Set<Integer> array = clientMapReducer.execute(new EratosthenesTask(102));

        for (Integer i : array) {
            System.out.print(i + " ");
        }
    }

}
