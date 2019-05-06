package elevator;

import java.util.*;

class ControllerAlpha implements Controller {

    private static int requestNumber = 0;

    private HashMap<Integer, Direction> floorRequests = new HashMap<>();
    private HashMap<Integer, Integer> elevatorRequests = new HashMap<>();
    private HashMap<Integer, Direction> elevatorStates = new HashMap<>();


    @Override
    public void selectElevatorAndSendToFloor(int toFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        //TODO: do some selection algorithm
        /**
         * 1. Get the locations of all elevators
         * 2.
         * */
        int selectedElevatorId = 1;// + (requestNumber++ % 4);
        Elevator e = ElevatorController.getInstance().getElevatorById(selectedElevatorId);
        if(e.getLocation() != toFloorNumber) {//elevator was not already there.
            e.addNextStop(toFloorNumber);
            EventLogger.print("Next Stop added to queue " + toFloorNumber);
            e.move();
        } else {//elevator was already there.

            executeLocationUpdate(e.getElevatorId(), toFloorNumber, desiredDirection);
        }
    }

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction elevatorServingDirection) throws ElevatorSystemException {
        Message message = new LocationUpdateMessage(elevatorId, elevatorLocation, elevatorServingDirection);
        Building.getInstance().notifyObservers(message);
    }

    @Override
    public void executeElevatorRequest(int elevatorId, int destinationFloor) throws ElevatorSystemException  {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        e.addNextStop(destinationFloor);
        e.move();
        if(e.getNextFloorQueue().isEmpty()) {
            setIdleAndReturnToDefaultFloor(e.getElevatorId());
            EventLogger.print("QUEUE is empty. Elevator will be idled....");
        }
    }

    @Override
    public void executeFloorRequest() throws ElevatorSystemException  {
        HashMap<Integer, Direction> table = getFloorRequests();
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
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction elevatorServingDirection) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        EventLogger.print("E" + elevatorId + " is at F" + elevatorLocation);
        if(!e.getNextFloorQueue().isEmpty()) {
            if(e.peekNextStop() == elevatorLocation) {
                EventLogger.print("Stop there and open for rider.");
                //
            }
        }
        announceLocationOfElevator(elevatorId, elevatorLocation, elevatorServingDirection);

    }

    @Override
    public void saveFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        HashMap<Integer, Direction> table = getFloorRequests();
        if(table.get(fromFloorNumber) != direction) {
            table.put(fromFloorNumber, direction);
            EventLogger.print("Floor Request (" + fromFloorNumber + ", " + direction + ") saved.");
        }
        executeFloorRequest();
    }
    @Override
    public void removeFloorRequest(int fromFloorNumber, Direction direction, int elevatorBoardedOn) throws ElevatorSystemException {
        //FloorRequest floorRequest = (FloorRequest) message;
       // int fromFloorNumber = floorRequest.getFromFloorNumber();
        //Direction direction = floorRequest.getDesiredDirection();
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorBoardedOn);
        //next stop is reached and should be deleted from queue.
        e.pollNextStop();
        HashMap<Integer, Direction> table = getFloorRequests();
        if(table.containsKey(fromFloorNumber)) {
            table.remove(fromFloorNumber, direction);
            EventLogger.print("Floor Request (" + fromFloorNumber + ", " + direction + ") removed.");
        }
    }

    void setIdleAndReturnToDefaultFloor(int elevatorId) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        e.setIdle();
        e.addNextStop(Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor")));
        e.move();
    }

    private HashMap<Integer, Direction> getFloorRequests() {
        return floorRequests;
    }

    boolean requestExists(int floorNumber, Direction desiredDirection) {
        if(getFloorRequests().containsKey(floorNumber) && getFloorRequests().get(floorNumber) == desiredDirection) {
            return true;
        }
        return false;
    }
    boolean atLeastOneRequest() {
        if(getFloorRequests().isEmpty()) {
            return false;
        }
        return true;
    }
}
