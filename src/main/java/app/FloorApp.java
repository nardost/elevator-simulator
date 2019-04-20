package app;

import elevator.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FloorApp {
    public static void main(String args[]) {
        try {
            Building building = Building.getInstance();
            if(building != null) {
                building.start();
            } else {
                System.out.println("Building null");
            }
        } catch(ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
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
