package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    private static final int MAX_PANCAKES = 12;
    private static final int MAX_PANCAKES_PER_USER = 5;
    private static final int NUM_USERS = 3;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("Starting Time: " + startTime.format(formatter));

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_USERS);
        CountDownLatch countDownLatch = new CountDownLatch(NUM_USERS);
        Semaphore pancakeSemaphore = new Semaphore(MAX_PANCAKES);

        int[] pancakeOrders = new int[NUM_USERS];
        for (int i = 0; i < NUM_USERS; i++) {
            pancakeOrders[i] = random.nextInt(MAX_PANCAKES_PER_USER + 1);
        }

        Future<Integer>[] pancakeEatTasks = new Future[NUM_USERS];
        for (int i = 0; i < NUM_USERS; i++) {
            final int userIndex = i;
            pancakeEatTasks[i] = executorService.submit(() -> {
                int pancakesToEat = Math.min(pancakeOrders[userIndex], MAX_PANCAKES_PER_USER);
                try {
                    pancakeSemaphore.acquire(pancakesToEat);
                    return pancakesToEat;
                } catch (InterruptedException e) {
                    return 0;
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        int totalPancakesEaten = 0;
        int totalPancakeWaste = 0;
        boolean isAllOrdersMet = true;

        for (int i = 0; i < NUM_USERS; i++) {
            try {
                totalPancakesEaten += pancakeEatTasks[i].get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
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
        executorService.shutdown();
    }
}
