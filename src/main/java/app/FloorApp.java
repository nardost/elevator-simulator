package app;

import elevator.*;

public class FloorApp {
    public static void main(String args[]) {
        try {
            Building building = Building.setupBuilding(10, 3, "alpha", "FILE");
            if(building != null) {
                building.start();
                System.out.println("Building created");
            } else {
                System.out.println("Building null");
            }
        } catch(ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }

    }
}
