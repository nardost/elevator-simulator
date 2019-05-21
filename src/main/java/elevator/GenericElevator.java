package elevator;

public interface GenericElevator {
    void move() throws ElevatorSystemException;
    int getLocation();
    Direction getDirection();
}
