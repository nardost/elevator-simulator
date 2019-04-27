package elevator;

import java.util.Hashtable;
import java.util.PriorityQueue;

/**
 *
 * @author ntessema
 *
 */
public interface Controller {
    void selectElevatorAndSendToFloor(int toFloorNumber, Direction desiredDirection) throws ElevatorSystemException;
    void announceLocationOfElevator(Message message) throws ElevatorSystemException;
    void executeElevatorRequest(int elevatorId, PriorityQueue<Integer> queue) throws ElevatorSystemException;
    void executeFloorRequest(Hashtable<Integer, Direction> table) throws ElevatorSystemException;
    void receiveLocationUpdateMessage(Message message) throws ElevatorSystemException;
}
