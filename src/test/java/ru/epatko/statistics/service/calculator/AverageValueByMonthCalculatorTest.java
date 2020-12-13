package ru.epatko.statistics.service.calculator;

import org.junit.Test;
import ru.epatko.statistics.model.Quotation;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static ru.epatko.statistics.service.Parser.INST_2;
import static ru.epatko.statistics.utils.AppUtils.DATE_FORMATTER;
import static ru.epatko.statistics.utils.AppUtils.YEAR_MONTH_FORMATTER;

public class AverageValueByMonthCalculatorTest {

    @Test
    public void shouldCalculateAvgByMonth() throws Exception {
        Quotation q1 = new Quotation(INST_2, LocalDate.parse("23-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q2 = new Quotation(INST_2, LocalDate.parse("24-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q3 = new Quotation(INST_2, LocalDate.parse("21-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q4 = new Quotation(INST_2, LocalDate.parse("22-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q5 = new Quotation(INST_2, LocalDate.parse("20-Feb-2013", DATE_FORMATTER), 1d);
        Quotation q6 = new Quotation(INST_2, LocalDate.parse("21-Jan-2013", DATE_FORMATTER), 1d);
        Quotation q7 = new Quotation(INST_2, LocalDate.parse("22-Nov-2013", DATE_FORMATTER), 1d);
        Quotation q8 = new Quotation(null, null, 0d);
        YearMonth byMonth = YearMonth.parse("Feb-2013", YEAR_MONTH_FORMATTER);
        BlockingQueue<Quotation> queue = new LinkedBlockingQueue<>(Arrays.asList(q1, q2, q3, q4, q5, q6, q7, q8));
        double expected = Stream.of(q1, q2, q3, q4, q5)
            .mapToDouble(Quotation::getValue)
            .average()
            .getAsDouble();

        AverageValueByMonthCalculator calculator = new AverageValueByMonthCalculator(queue, byMonth);
        Double result = calculator.call();

        assertEquals(expected, result, 0.0);
    }

    @Test
    public void whenNothingToCalculate_shouldNotDeadLock() throws Exception {
        BlockingQueue<Quotation> queue = new LinkedBlockingQueue<>();
        queue.put(new Quotation(null, null, 0d));
        YearMonth byMonth = YearMonth.parse("Feb-2013", YEAR_MONTH_FORMATTER);

        AverageValueByMonthCalculator calculator = new AverageValueByMonthCalculator(queue, byMonth);
        Double result = calculator.call();

        assertEquals(0, result, 0.0);
    }
}