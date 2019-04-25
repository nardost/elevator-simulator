package app;

import elevator.*;
import gui.ElevatorDisplay;

import java.util.concurrent.TimeUnit;

public class FloorApp {
    public static void main(String args[]) {

        System.out.println("Starting up...");
        try {
            Building building = Building.getInstance();
            int numFloor = building.getNumberOfFloors();
            int numElev = building.getNumberOfElevators();

            ElevatorDisplay.getInstance().initialize(numFloor);
            for (int i = 1; i <= numElev; i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }

            if(building != null) {
                building.getInstance().generatePerson(9, 1);
                building.getInstance().generatePerson(20, 1);
                building.getInstance().generatePerson(2, 1);
                building.getInstance().generatePerson(2, 18);
                building.getInstance().generatePerson(12, 16);
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
