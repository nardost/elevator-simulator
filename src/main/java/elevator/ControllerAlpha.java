package elevator;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

class ControllerAlpha implements Controller {

    private static int requestNumber = 0;


    @Override
    public void selectElevatorAndSendToFloor(int toFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        //TODO: do some selection algorithm
        int selectedElevatorId = 1 + (requestNumber++ % 4);
        ElevatorController.getInstance().commandElevator(selectedElevatorId, toFloorNumber, desiredDirection);
    }

    @Override
    public void announceLocationOfElevator(Message message) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(message);
    }

    @Override
    public void executeElevatorRequest(int elevatorId, PriorityQueue<Integer> queue) throws ElevatorSystemException  {
        if(queue.isEmpty()) {
            ElevatorController.getInstance().setIdleAndReturnToDefaultFloor(elevatorId);
        }
    }

    @Override
    public void executeFloorRequest(Hashtable<Integer, Direction> table) throws ElevatorSystemException  {
        Iterator<Integer> iterator = table.keySet().iterator();
        int i = 0;
        while(iterator.hasNext()) {
            EventLogger.print("Floor Request " + (++i));
            int floor = iterator.next();
            Direction direction = table.get(floor);
            selectElevatorAndSendToFloor(floor, direction);
        }
    }

    @Override
    public void receiveLocationUpdateMessage(Message request) throws ElevatorSystemException {

        LocationUpdateMessage message = (LocationUpdateMessage) request;

        int elevatorId = message.getElevatorId();
        int elevatorLocation = message.getElevatorLocation();
        Direction elevatorServingDirection = message.getServingDirection();

        EventLogger.print("E" + elevatorId + " is at F" + elevatorLocation);
        announceLocationOfElevator(new LocationUpdateMessage(elevatorId, elevatorLocation, elevatorServingDirection));

    }

}
