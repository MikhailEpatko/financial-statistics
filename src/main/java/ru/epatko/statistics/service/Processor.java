package ru.epatko.statistics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.epatko.statistics.model.Quotation;
import ru.epatko.statistics.service.calculator.AverageValueByMonthCalculator;
import ru.epatko.statistics.service.calculator.AverageValueCalculator;
import ru.epatko.statistics.service.calculator.LastTenSumCalculator;
import ru.epatko.statistics.utils.BoundedPriorityBlockingQueue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static ru.epatko.statistics.Application.printError;
import static ru.epatko.statistics.service.Parser.*;
import static ru.epatko.statistics.utils.AppUtils.DATE_FORMATTER;
import static ru.epatko.statistics.utils.AppUtils.YEAR_MONTH_FORMATTER;

public class Processor {

    private static final Logger log = LoggerFactory.getLogger(Processor.class);
    private final String lastDateString;
    private final String byMonthString;
    private final String path;

    public Processor(String lastDateString, String byMonthString, String path) {
        this.lastDateString = lastDateString;
        this.byMonthString = byMonthString;
        this.path = path;
    }

    public void start() {
        try {
            LocalDate lastDate = LocalDate.parse(lastDateString, DATE_FORMATTER);
            YearMonth byMonth = YearMonth.parse(byMonthString, YEAR_MONTH_FORMATTER);

            CountDownLatch latch = new CountDownLatch(1);

            Map<String, BlockingQueue<Quotation>> instrumentToQueue = new HashMap<>(4);
            BlockingQueue<Quotation> queue1 = new LinkedBlockingQueue<>();
            BlockingQueue<Quotation> queue2 = new LinkedBlockingQueue<>();
            BlockingQueue<Quotation> queue3 = new BoundedPriorityBlockingQueue<>(10);
            instrumentToQueue.put(INST_1, queue1);
            instrumentToQueue.put(INST_2, queue2);
            instrumentToQueue.put(INST_3, queue3);

            ExecutorService pool = Executors.newFixedThreadPool(4);
            Callable<Void> parser = new Parser(latch, path, lastDate, instrumentToQueue);
            Callable<Double> avgCalculator = new AverageValueCalculator(queue1);
            Callable<Double> avgByMonthCalculator = new AverageValueByMonthCalculator(queue2, byMonth);
            Callable<Double> lastTenSumCalculator = new LastTenSumCalculator(queue3, latch);

            Future<Void> parserJob = pool.submit(parser);
            Future<Double> avgFuture = pool.submit(avgCalculator);
            Future<Double> avgByMonthFuture = pool.submit(avgByMonthCalculator);
            Future<Double> lastTenSumFuture = pool.submit(lastTenSumCalculator);

            parserJob.get();
            double avg = avgFuture.get();
            double avgByMonth = avgByMonthFuture.get();
            double lastTenSum = lastTenSumFuture.get();

            pool.shutdownNow();

            System.out.println("AVG: " + avg);
            System.out.println("AVG by month: " + avgByMonth);
            System.out.println("Last ten sum: " + lastTenSum);
        } catch (Exception e) {
            log.error("Got Exception: ", e);
            printError();
        }
    }
}
