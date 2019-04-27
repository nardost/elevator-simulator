package elevator;

import java.util.Hashtable;
import java.util.PriorityQueue;

class ControllerBeta implements Controller {

    @Override
    public void selectElevatorAndSendToFloor(int forFloorNumber, Direction desiredDirection) throws ElevatorSystemException {

    }

    @Override
    public void announceLocationOfElevator(Message message) throws ElevatorSystemException {

    }


    @Override
    public void executeElevatorRequest(int elevatorId, PriorityQueue<Integer> queue) throws ElevatorSystemException {

    }

    @Override
    public void executeFloorRequest(Hashtable<Integer, Direction> table) throws ElevatorSystemException {

    }

    @Override
    public void receiveLocationUpdateMessage(Message message) throws ElevatorSystemException {

    }
}
