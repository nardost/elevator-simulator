
package app;

import elevator.Building;
import elevator.ElevatorSystemException;
import gui.ElevatorDisplay;
import static gui.ElevatorDisplay.Direction.*;


public class Main {

    public static void main(String[] args) throws InterruptedException, ElevatorSystemException {


        Building building = Building.getInstance();

        int numFloor = building.getNumberOfFloors();
        int numElev = building.getNumberOfElevators();

        ElevatorDisplay.getInstance().initialize(numFloor);
        for (int i = 1; i <= numElev; i++) {
            ElevatorDisplay.getInstance().addElevator(i, 1);
        }

        // Go up
        for (int j = 1; j <= numElev; j++) {
            moveElevator(j, 1, numFloor);
            ElevatorDisplay.getInstance().setIdle(j);
        }
        Thread.sleep(1000);

        // Go down
        for (int j = 1; j <= numElev; j++) {
            moveElevator(j, numFloor, 1);
            ElevatorDisplay.getInstance().setIdle(j);
        }
        Thread.sleep(1000);

        // Go up halfway
        moveElevator(1, 1, numFloor / 2);
        Thread.sleep(1000);

        // Go up remaining floors
        moveElevator(1, numFloor / 2, numFloor);
        Thread.sleep(1000);

        for (int i = numFloor; i > 1; i--) {
            moveElevator(1, i, i - 1);
            Thread.sleep(500);
        }
        ElevatorDisplay.getInstance().setIdle(1);
        Thread.sleep(1000);

        ElevatorDisplay.getInstance().shutdown();
    }

    private static void moveElevator(int elevNum, int fromFloor, int toFloor) throws InterruptedException {
        int numRiders = (int) (11.0 * Math.random()) + 1;

        ElevatorDisplay.getInstance().closeDoors(elevNum);
        if (fromFloor < toFloor) {
            for (int i = fromFloor; i <= toFloor; i++) {
                ElevatorDisplay.getInstance().updateElevator(elevNum, i, numRiders, UP);
                Thread.sleep(80);
            }
        } else {
            for (int i = fromFloor; i >= toFloor; i--) {
                ElevatorDisplay.getInstance().updateElevator(elevNum, i, numRiders, DOWN);
                Thread.sleep(80);
            }
        }
        ElevatorDisplay.getInstance().openDoors(elevNum);
    }
}
