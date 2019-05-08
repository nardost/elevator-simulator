package elevator;

        import gui.ElevatorDisplay;

public class Test3 {
    public static void main(String[] args) {
        Building.TEST = 3;
        try {
            EventLogger.print("---------------------- TEST 3 - STARTING UP --------------------------");

            int numFloor = Building.getInstance().getNumberOfFloors();
            int numElev = Building.getInstance().getNumberOfElevators();
            ElevatorDisplay.getInstance().initialize(numFloor);
            for (int i = 1; i <= numElev; i++) {
                ElevatorDisplay.getInstance().addElevator(i, 1);
            }
            Building.getInstance().generatePerson(20, 1);

            EventLogger.print("---------------------- TEST 3 - DONE ---------------------------------");
        } catch (ElevatorSystemException ese) {
            System.out.println(ese.getMessage());
        }
    }
}
