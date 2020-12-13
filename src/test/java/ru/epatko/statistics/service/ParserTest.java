package ru.epatko.statistics.service;

import org.junit.Test;
import ru.epatko.statistics.model.Quotation;
import ru.epatko.statistics.utils.BoundedPriorityBlockingQueue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.*;
import static ru.epatko.statistics.service.Parser.*;
import static ru.epatko.statistics.utils.AppUtils.DATE_FORMATTER;

public class ParserTest {

    @Test
    public void test_parseFile_shouldParseCorrectly() {
        String path = getClass().getClassLoader().getResource("test.txt").getPath();
        LocalDate lastDate = LocalDate.parse("27-Feb-2013", DATE_FORMATTER);
        Map<String, BlockingQueue<Quotation>> instrumentToQueue = new HashMap<>(4);
        BlockingQueue<Quotation> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Quotation> queue2 = new LinkedBlockingQueue<>();
        BlockingQueue<Quotation> queue3 = new BoundedPriorityBlockingQueue<>(10);
        instrumentToQueue.put(INST_1, queue1);
        instrumentToQueue.put(INST_2, queue2);
        instrumentToQueue.put(INST_3, queue3);
        LocalDate saturday = LocalDate.parse("23-Feb-2013", DATE_FORMATTER);
        LocalDate sunday = LocalDate.parse("24-Feb-2013", DATE_FORMATTER);
        CountDownLatch latch = new CountDownLatch(1);

        Parser parser = new Parser(latch, path, lastDate, instrumentToQueue);

        assertEquals(1L, latch.getCount());

        parser.call();

        assertEquals(0L, latch.getCount());
        assertEquals(17, queue1.size());
        assertEquals(17, queue2.size());
        assertEquals(10, queue3.size());

        for (BlockingQueue<Quotation> queue : instrumentToQueue.values()) {
            for (Quotation quotation : queue) {
                assertNotEquals(quotation.getDate(), saturday);
                assertNotEquals(quotation.getDate(), sunday);
                if (quotation.getDate() != null) {
                    assertTrue(quotation.getDate().isBefore(lastDate) || quotation.getDate().equals(lastDate));
                }
            }
        }

        List<Quotation> polledElements1 = new ArrayList<>();
        List<Quotation> polledElements2 = new ArrayList<>();
        queue1.drainTo(polledElements1);
        queue2.drainTo(polledElements2);

        assertNull(polledElements1.get(polledElements1.size() - 1).getDate());
        assertNull(polledElements1.get(polledElements1.size() - 1).getInstrument());
        assertNull(polledElements2.get(polledElements2.size() - 1).getDate());
        assertNull(polledElements2.get(polledElements2.size() - 1).getInstrument());
    }

    @Test
    public void test_parseFile_shouldSkipWrongData() {
        String path = getClass().getClassLoader().getResource("error_test.txt").getPath();
        LocalDate lastDate = LocalDate.parse("27-Feb-2013", DATE_FORMATTER);
        Map<String, BlockingQueue<Quotation>> instrumentToQueue = new HashMap<>(4);
        BlockingQueue<Quotation> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Quotation> queue2 = new LinkedBlockingQueue<>();
        BlockingQueue<Quotation> queue3 = new BoundedPriorityBlockingQueue<>(10);
        instrumentToQueue.put(INST_1, queue1);
        instrumentToQueue.put(INST_2, queue2);
        instrumentToQueue.put(INST_3, queue3);
        Quotation expected = new Quotation("INSTRUMENT1", LocalDate.parse("20-Feb-2013", DATE_FORMATTER), 1d);
        CountDownLatch latch = new CountDownLatch(1);

        Parser parser = new Parser(latch, path, lastDate, instrumentToQueue);

        assertEquals(1L, latch.getCount());

        parser.call();

        assertEquals(0L, latch.getCount());
        assertEquals(2, queue1.size());
        assertEquals(1, queue2.size());
        assertEquals(1, queue3.size());

        List<Quotation> polledElements1 = new ArrayList<>();
        queue1.drainTo(polledElements1);

        assertEquals(expected, polledElements1.get(0));
        assertNull(polledElements1.get(polledElements1.size() - 1).getDate());
        assertNull(polledElements1.get(polledElements1.size() - 1).getInstrument());
    }
}