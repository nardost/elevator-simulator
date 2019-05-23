package elevator;

import gui.ElevatorDisplay;

public class Driver {
    public static void main(String[] args) {
        try {
            EventLogger.print("......................................Elevator Simulation Starting Up....................................");
            Building building = Building.getInstance();
            ElevatorDisplay.getInstance().initialize(building.getNumberOfFloors());
            for (int i = 1; i <= building.getNumberOfElevators(); i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }
            building.start();
            EventLogger.print(building.generateReport());
            EventLogger.print("......................................Elevator Simulation Completed.......................................");
        } catch(ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }
    }
}
