package ru.epatko.statistics.service.calculator;

import ru.epatko.statistics.model.Quotation;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class LastTenSumCalculator implements Callable<Double> {

    private final BlockingQueue<Quotation> queue;
    private final CountDownLatch latch;

    public LastTenSumCalculator(BlockingQueue<Quotation> queue, CountDownLatch latch) {
        this.queue = queue;
        this.latch = latch;
    }

    public Double call() throws InterruptedException {
        latch.await();
        double sum = 0D;
        Quotation quotation = queue.poll();
        while (quotation != null && quotation.getDate() != null) {
            sum = sum + quotation.getValue();
            quotation = queue.poll();
        }
        return sum;
    }
}
