package elevator;

/**
 * Strategy
 */
public interface Controller {
    void run() throws ElevatorSystemException;
    void executeElevatorRequest(int elevatorId, int personId, int destinationFloor, int fromFloorNumber) throws ElevatorSystemException;
    void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException;
    void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException;
    void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction elevatorServingDirection, Direction directionDispatchedFor) throws ElevatorSystemException;
    void exitRider(int elevatorId, int personId, int intFloorNumber) throws ElevatorSystemException;
    String unhandledFloorRequests() throws ElevatorSystemException;
}
