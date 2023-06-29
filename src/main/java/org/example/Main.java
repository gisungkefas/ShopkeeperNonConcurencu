package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int MAX_PANCAKES = 12;
    private static final int MAX_PANCAKES_PER_USER = 5;
    private static final int NUM_USERS = 3;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("Starting Time: " + startTime.format(formatter));

        ForkJoinPool forkJoinPool = new ForkJoinPool(NUM_USERS);

        int[] pancakeOrders = new int[NUM_USERS];
        for (int i = 0; i < NUM_USERS; i++) {
            pancakeOrders[i] = random.nextInt(MAX_PANCAKES_PER_USER + 1);
        }

        Semaphore pancakeSemaphore = new Semaphore(MAX_PANCAKES);

        PancakeEatTask[] pancakeEatTasks = new PancakeEatTask[NUM_USERS];
        for (int i = 0; i < NUM_USERS; i++) {
            pancakeEatTasks[i] = new PancakeEatTask(i, pancakeOrders, pancakeSemaphore, MAX_PANCAKES_PER_USER);
            forkJoinPool.execute(pancakeEatTasks[i]);
        }

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        int totalPancakesEaten = 0;
        int totalPancakeWaste = 0;
        boolean isAllOrdersMet = true;

        for (int i = 0; i < NUM_USERS; i++) {
            totalPancakesEaten += pancakeEatTasks[i].join();
            if (pancakeOrders[i] > MAX_PANCAKES_PER_USER) {
                totalPancakeWaste += pancakeOrders[i] - MAX_PANCAKES_PER_USER;
                isAllOrdersMet = false;
            }
        }

        LocalDateTime endTime = LocalDateTime.now();
        System.out.println("Ending Time: " + endTime.format(formatter));

        System.out.println("Shopkeeper successfully met the needs of the 3 users: " + isAllOrdersMet);
        System.out.println("Total pancakes eaten: " + totalPancakesEaten);
        System.out.println("Total pancakes wasted: " + totalPancakeWaste);
        System.out.println("Number of pancake orders not met: " + (isAllOrdersMet ? 0 : NUM_USERS));
    }
}