package elevator;

import gui.ElevatorDisplay;

public class Test1 {
    public static void main(String[] args) {
        Building.TEST = 1;
        try {
            EventLogger.print("---------------------- TEST 1 - STARTING UP --------------------------");

            int numFloor = Building.getInstance().getNumberOfFloors();
            int numElev = Building.getInstance().getNumberOfElevators();
            ElevatorDisplay.getInstance().initialize(numFloor);
            for (int i = 1; i <= numElev; i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }
            Building.getInstance().generatePerson(1, 10);

            EventLogger.print("---------------------- TEST 1 - DONE ---------------------------------");
        } catch (ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }
    }
}
