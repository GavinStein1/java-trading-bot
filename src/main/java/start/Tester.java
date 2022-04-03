package src.main.java.start;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

public class Tester {

    public static void main (String[] args) {

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Tester::myTask, 0, 1, TimeUnit.SECONDS);
    }

    private static void myTask() {
        System.out.println("Running");
        double f = 0.00025;
        double g = 0.00027;
        double h = (f + g)/2;
        System.out.println(String.format("%.5f", h));
    }
    
}