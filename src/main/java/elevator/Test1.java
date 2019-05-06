package elevator;

import gui.ElevatorDisplay;

public class Test1 {
    public static void main(String[] args) {
        try {
            ElevatorController controller = ElevatorController.getInstance();
            int numFloor = Building.getInstance().getNumberOfFloors();
            int numElev = Building.getInstance().getNumberOfElevators();
            ElevatorDisplay.getInstance().initialize(numFloor);
            for (int i = 1; i <= numElev; i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }
            Elevator elevator = controller.getElevatorById(1);
            Building.getInstance().generatePerson(20, 5);
            elevator.move();
        } catch (ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }
    }
}
