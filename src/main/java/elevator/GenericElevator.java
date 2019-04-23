package elevator;

public interface GenericElevator {
    int getLocation();
    int getSpeed();
    Direction getDirection();
    boolean areDoorsClosed();
    void moveTo(int floor, Direction direction) throws  ElevatorSystemException;
    void openDoors() throws ElevatorSystemException;
    void closeDoors();
}
