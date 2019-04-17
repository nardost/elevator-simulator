package elevator;

import java.util.Observer;

public interface ElevatorBehavior {

    int getLocation();
    double getSpeed();
    Motion getMotion();
    Doors getDoorsStatus();

    void run() throws ElevatorSystemException;
    void move(int floor) throws  ElevatorSystemException;
    void openDoors();
    void closeDoors();
}
