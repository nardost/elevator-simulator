package elevator;

import java.util.HashMap;

/**
 * Strategy
 */
public interface Controller {
    void selectElevatorAndSendToFloor(int toFloorNumber, Direction desiredDirection) throws ElevatorSystemException;
    void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction elevatorServingDirection) throws ElevatorSystemException;
    void saveFloorRequest(int floorNumber, Direction direction) throws  ElevatorSystemException;
    void removeFloorRequest(int fromFloor, Direction directionRequested, int elevatorBoardedOn) throws ElevatorSystemException;
    void executeElevatorRequest(int elevatorId, int destinationFloor) throws ElevatorSystemException;
    void executeFloorRequest() throws ElevatorSystemException;
    void executeLocationUpdate(int elevatorId, int toFloorNumber, Direction direction) throws ElevatorSystemException;
}
