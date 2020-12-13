package ru.epatko.statistics.service.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.epatko.statistics.model.Quotation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;


public class AverageValueCalculator implements Callable<Double> {

    private static final Logger log = LoggerFactory.getLogger(AverageValueCalculator.class);
    private final BlockingQueue<Quotation> queue;

    public AverageValueCalculator(BlockingQueue<Quotation> queue) {
        this.queue = queue;
    }

    public Double call() throws Exception {
        double sum = 0D;
        long count = 0L;
        Quotation quotation = queue.take();
        while (quotation.getDate() != null) {
            sum += quotation.getValue();
            count++;
            quotation = queue.take();
        }
        return count == 0 ? 0 : sum / count;
    }
}
