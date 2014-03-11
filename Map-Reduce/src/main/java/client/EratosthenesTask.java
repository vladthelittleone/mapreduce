package client;

import client.task.Reducible;

import java.util.*;

/**
 * Project: Map-Reduce
 * Date: 10.03.14
 * Time: 13:33
 *
 * @author Skurishin Vladislav
 */
public class EratosthenesTask implements ClientMapReducer.ReducibleTask<Integer[]>{
    private int start;
    private int end;

    public EratosthenesTask(int end){
        this.start = 0;
        this.end = end;
    }

    private EratosthenesTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Set<Reducible<Integer[]>> getSubTasks(int fragments) {
        Set<Reducible<Integer[]>> tasks = new HashSet<>(fragments);

        System.out.println("FUCK THIS SHIT");

        int range = (end - start) + 1;
        int step = 1;
        int rest = 0;

        if (range < fragments){
            fragments = range;
        } else {
            step = range / fragments;
            rest = range % fragments;
        }

        int s = end + 1;

        for (int i = 1; i < fragments; i++){

            int rStep = step * i;
            if (rest != 0){
                rStep++;
                rest--;
            }

            s =- rStep * i;

            tasks.add(new EratosthenesTask(s, s + rStep - 1));
        }

        return tasks;
    }

    @Override
    public Integer[] result() {
        System.out.println("FUCK THIS SHIT");
        return primes(start, end);
    }

    private Integer[] primes(int start, int end){
        return new Integer[end - start];
    }

}
