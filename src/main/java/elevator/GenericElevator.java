package elevator;

public interface GenericElevator {
    void move() throws ElevatorSystemException;
    void stop() throws ElevatorSystemException;
    int getLocation();
    Direction getDirection();
}
