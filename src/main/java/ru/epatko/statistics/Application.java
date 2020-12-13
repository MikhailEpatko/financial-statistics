package ru.epatko.statistics;

import ru.epatko.statistics.service.Processor;

public class Application {

    public static void main(String[] args) {
        if (args.length == 3) {
            String lastDate = args[0];
            String path = args[1];
            String byMonth = args[2];
            Processor processor = new Processor(lastDate, byMonth, path);
            processor.start();
        } else {
            printError();
        }
    }

    public static void printError() {
        System.out.println();
        System.out.println("Required arguments:");
        System.out.println("  java -jar ./application.jar lastDate path byMonth");
        System.out.println();
        System.out.println("  lastDate - date until we need calculate the statistics,");
        System.out.println("  path     - path to the data file,");
        System.out.println("  byMonth  - month we need calculate the statistics.");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  java -jar ./application.jar 19-Dec-2014 path/to/the/data-file.txt Feb-2013");
    }
}
