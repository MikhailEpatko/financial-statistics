package ru.epatko.statistics.service;

import org.junit.Test;
import ru.epatko.statistics.Application;

public class ProcessorIntegrationTest {

    @Test
    public void start() throws Exception {
        String lastDate = "23-Feb-2013";
        String path = Application.class.getClassLoader().getResource("test.txt").getPath();
        String byMonth = "Feb-2013";

        Processor processor = new Processor(lastDate, byMonth, path);
        processor.start();
    }
}