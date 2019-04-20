package elevator;

public interface GenericElevator {
    int getLocation();
    double getSpeed();
    Direction getDirection();
    boolean areDoorsClosed();
    void move(int floor) throws  ElevatorSystemException;
    void openDoors();
    void closeDoors();
}
