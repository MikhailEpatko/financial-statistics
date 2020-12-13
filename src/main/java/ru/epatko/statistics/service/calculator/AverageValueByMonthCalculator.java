package ru.epatko.statistics.service.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.epatko.statistics.model.Quotation;
import java.time.YearMonth;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class AverageValueByMonthCalculator implements Callable<Double> {

    private static final Logger log = LoggerFactory.getLogger(AverageValueByMonthCalculator.class);
    private final BlockingQueue<Quotation> queue;
    private final YearMonth byMonth;

    public AverageValueByMonthCalculator(BlockingQueue<Quotation> queue, YearMonth byMonth) {
        this.queue = queue;
        this.byMonth = byMonth;
    }

    public Double call() throws Exception {
        double sum = 0D;
        long count = 0L;
        Quotation quotation = queue.take();
        while (quotation.getDate() != null) {
            if (quotation.getDate().getYear() == byMonth.getYear() && quotation.getDate().getMonth() == byMonth.getMonth()) {
                sum = sum + quotation.getValue();
                count++;
            }
            quotation = queue.take();
        }
        return count == 0 ? 0D : sum / count;
    }
}
