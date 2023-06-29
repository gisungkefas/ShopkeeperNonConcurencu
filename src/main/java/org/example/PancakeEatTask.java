package org.example;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.Semaphore;

public class PancakeEatTask extends RecursiveTask<Integer> {

    private int userIndex;
    private int[] pancakeOrders;
    private Semaphore pancakeSemaphore;
    private int MAX_PANCAKES_PER_USER;

    public PancakeEatTask(int userIndex, int[] pancakeOrders, Semaphore pancakeSemaphore, int MAX_PANCAKES_PER_USER) {
        this.userIndex = userIndex;
        this.pancakeOrders = pancakeOrders;
        this.pancakeSemaphore = pancakeSemaphore;
        this.MAX_PANCAKES_PER_USER = MAX_PANCAKES_PER_USER;
    }

    @Override
    protected Integer compute() {
        int pancakesToEat = Math.min(pancakeOrders[userIndex], MAX_PANCAKES_PER_USER);
        try {
            pancakeSemaphore.acquire(pancakesToEat);
            return pancakesToEat;
        } catch (InterruptedException e) {
            return 0;
        }
    }
}
