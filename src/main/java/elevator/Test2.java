package elevator;

import gui.ElevatorDisplay;

public class Test2 {
    public static void main(String[] args) {
        Building.TEST = 2;
        try {
            EventLogger.print("---------------------- TEST 2 - STARTING UP --------------------------");

            int numFloor = Building.getInstance().getNumberOfFloors();
            int numElev = Building.getInstance().getNumberOfElevators();
            ElevatorDisplay.getInstance().initialize(numFloor);
            for (int i = 1; i <= numElev; i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }
            Building.getInstance().generatePerson(20, 5);

            EventLogger.print("---------------------- TEST 2 - DONE ---------------------------------");
        } catch (ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }
    }
}
