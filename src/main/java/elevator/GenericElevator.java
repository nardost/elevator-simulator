package elevator;

public interface GenericElevator {
    int getLocation();
    int getSpeed();
    Direction getDirection();
    boolean areDoorsClosed();
    void openDoors() throws ElevatorSystemException;
    void closeDoors();
}
