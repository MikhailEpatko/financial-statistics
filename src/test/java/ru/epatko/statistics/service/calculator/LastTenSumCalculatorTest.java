package ru.epatko.statistics.service.calculator;

import org.junit.Test;
import ru.epatko.statistics.model.Quotation;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static ru.epatko.statistics.service.Parser.INST_2;
import static ru.epatko.statistics.utils.AppUtils.DATE_FORMATTER;

public class LastTenSumCalculatorTest {

    @Test
    public void shouldCalculateLastTenSum() throws InterruptedException {
        Quotation q1 = new Quotation(INST_2, LocalDate.parse("23-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q2 = new Quotation(INST_2, LocalDate.parse("24-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q3 = new Quotation(INST_2, LocalDate.parse("21-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q4 = new Quotation(INST_2, LocalDate.parse("22-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q5 = new Quotation(INST_2, LocalDate.parse("20-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q6 = new Quotation(INST_2, LocalDate.parse("21-Jan-2013", DATE_FORMATTER), 1d);
        Quotation q7 = new Quotation(INST_2, LocalDate.parse("22-Nov-2013", DATE_FORMATTER), 1d);
        Quotation q8 = new Quotation(INST_2, LocalDate.parse("22-Nov-2013", DATE_FORMATTER), 1d);
        Quotation q9 = new Quotation(INST_2, LocalDate.parse("22-Nov-2013", DATE_FORMATTER), 1d);
        Quotation q10 = new Quotation(INST_2, LocalDate.parse("22-Nov-2013", DATE_FORMATTER), 1d);

        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();

        BlockingQueue<Quotation> queue = new LinkedBlockingQueue<>(Arrays.asList(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10));
        double expected = Stream.of(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10)
            .mapToDouble(Quotation::getValue)
            .sum();

        LastTenSumCalculator calculator = new LastTenSumCalculator(queue, latch);
        Double result = calculator.call();

        assertEquals(expected, result, 0.0);
    }

    @Test
    public void whenNothingToCalculate_shouldNotDeadLock() throws InterruptedException {
        BlockingQueue<Quotation> queue = new LinkedBlockingQueue<>();
        queue.put(new Quotation(null, null, 0d));
        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();

        LastTenSumCalculator calculator = new LastTenSumCalculator(queue, latch);
        Double result = calculator.call();

        assertEquals(0, result, 0.0);
    }
}