package ru.epatko.statistics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.epatko.statistics.model.Quotation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static ru.epatko.statistics.utils.AppUtils.DATE_FORMATTER;

public class Parser implements Callable<Void> {

    private static final Logger log = LoggerFactory.getLogger(Parser.class);
    public static final String INST_1 = "INSTRUMENT1";
    public static final String INST_2 = "INSTRUMENT2";
    public static final String INST_3 = "INSTRUMENT3";
    private final CountDownLatch latch;
    private final String pathToTheFile;
    private final LocalDate untilDate;
    private final Map<String, BlockingQueue<Quotation>> instrumentToQueue;

    public Parser(CountDownLatch latch, String pathToTheFile, LocalDate untilDate, Map<String, BlockingQueue<Quotation>> instrumentToQueue) {
        this.latch = latch;
        this.pathToTheFile = pathToTheFile;
        this.untilDate = untilDate;
        this.instrumentToQueue = instrumentToQueue;
    }

    @Override
    public Void call() {
        log.debug("Start parser");
        try (BufferedReader br = new BufferedReader(new FileReader(pathToTheFile))) {
            br.lines()
                .map(line -> parse(line, untilDate))
                .filter(Objects::nonNull)
                .forEach(quotation -> addQuotation(quotation, instrumentToQueue));
            log.debug("All the Quotations have been added");
            for (BlockingQueue<Quotation> queue : instrumentToQueue.values()) {
                queue.put(new Quotation(null, null, 0D));
            }
            latch.countDown();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private Quotation parse(String line, LocalDate untilDate) {
        log.debug("Start pare line: [ {} ].", line);
        Quotation quotation = null;
        Set<String> instruments = instrumentToQueue.keySet();
        String[] strings = line.split(",");
        try {
            LocalDate date = LocalDate.parse(strings[1], DATE_FORMATTER);
            if (instruments.contains(strings[0])
                && date.getDayOfWeek() != DayOfWeek.SATURDAY
                && date.getDayOfWeek() != DayOfWeek.SUNDAY
                && (date.isBefore(untilDate) || date.equals(untilDate))) {
                quotation = new Quotation(strings[0], date, Double.parseDouble(strings[2]));
            }
        } catch (Exception e) {
            log.error("Error parsing line [ {} ]. Line was skipped. Check the data: ", line, e);
        }
        return quotation;
    }

    private void addQuotation(Quotation quotation, Map<String, BlockingQueue<Quotation>> instrumentToQueue) {
        log.debug("Start add Quotation: {}", quotation);
        BlockingQueue<Quotation> queue = instrumentToQueue.get(quotation.getInstrument());
        if (queue != null) {
            try {
                queue.put(quotation);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.debug("There is no queue for instrument: [ {} ].", quotation.getInstrument());
        }
        log.debug("Added Quotation");
    }
}
