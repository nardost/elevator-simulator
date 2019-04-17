package app;

import elevator.EventLogger;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ElevatorSimulationApp {

    public static void main(String [] args) {
        long start = System.nanoTime();
        EventLogger consoleLogger;
        EventLogger textFileLogger;
        String msg = UUID.randomUUID().toString();
        try {
            textFileLogger = EventLogger.getInstance("FILE");
            consoleLogger = EventLogger.getInstance("STDOUT");
            consoleLogger.logEvent(formatLog(System.nanoTime() - start, "Console Logger " + msg));
            textFileLogger.logEvent(formatLog(System.nanoTime() - start, "File Logger " + msg));
        } catch(Exception e) {
            System.out.println("Some error");
        }
    }

    private static String formatLog(long timestamp, String msg) {
        long secs = TimeUnit.NANOSECONDS.toSeconds(timestamp);
        long h = (secs - secs % 3600) / 60;
        long m = (secs % 3600 - secs % 60) / 60;
        long s = secs % 60;
        return String.format("%02d:%02d:%02d %s", h, m, s, msg);
    }
}
