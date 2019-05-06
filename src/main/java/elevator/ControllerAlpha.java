package elevator;

import java.util.*;

class ControllerAlpha implements Controller {

    private static int requestNumber = 0;

    private HashMap<Integer, Direction> floorRequests = new HashMap<>();
    private HashMap<Integer, Integer> elevatorRequests = new HashMap<>();
    private HashMap<Integer, Direction> elevatorStates = new HashMap<>();

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(elevatorId, elevatorLocation, direction, directionDispatchedFor);
    }

    @Override
    public void executeElevatorRequest(int elevatorId, int destinationFloor, int fromFloorNumber) throws ElevatorSystemException  {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        Direction direction = e.getDispatchedToServeDirection();
        removeFloorRequest(fromFloorNumber, direction, elevatorId);//it has been served...
        e.addNextStop(destinationFloor);
        e.move();
        if(e.getNextFloorQueue().isEmpty()) {
            EventLogger.print("E-" + e.getElevatorId() + " has no more next stops. It will be idled.");
            setIdleAndReturnToDefaultFloor(e.getElevatorId());
        }
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException  {
        saveFloorRequest(fromFloorNumber, direction);
        selectElevatorAndSendToFloor(fromFloorNumber, direction);
        /*
        Iterator<Integer> iterator = table.keySet().iterator();
        int i = 0;
        while(iterator.hasNext()) {
            EventLogger.print("Floor Request " + (++i));
            int floor = iterator.next();
            Direction direction = table.get(floor);
            selectElevatorAndSendToFloor(floor, direction);
        }*/
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        EventLogger.print("E-" + elevatorId + " is at F-" + elevatorLocation + ". Dispatched for F-" + e.getDispatchedForFloor() + ". Dispatched to serve " + e.getDispatchedToServeDirection().toString());
        if(!e.getNextFloorQueue().isEmpty()) {
            if(e.peekNextStop() == elevatorLocation) {
                e.openDoors();
                int f = e.pollNextStop();
                EventLogger.print("E-" + e.getElevatorId() + " at F-" + e.getLocation() + " and " + f + " was removed from queue.");
                if(elevatorLocation == e.getDispatchedForFloor()) {
                    e.setDirection(e.getDispatchedToServeDirection());
                    EventLogger.print("E-" + e.getElevatorId() + " making a U-turn at F-" + e.getLocation());
                }
                //TODO: stop, open doors, let them in, close doors and go...
            }
        }
        //TODO: next statement was before the if block above and soe next stops were not being popped out...
        announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    public void selectElevatorAndSendToFloor(int toFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        //TODO: do some selection algorithm
        /**
         * 1. Get the locations of all elevators
         * 2.
         * */
        int selectedElevatorId;// + (requestNumber++ % 4);
        int secondElevatorId = 4;
        switch(Building.TEST) {
            case 1:
            case 4:
                selectedElevatorId = 1;
                break;
            case 2:
                selectedElevatorId = 2;
                break;
            case 3:
                selectedElevatorId = 3;
                break;
            default:
                selectedElevatorId = 4;
                break;
        }
        Elevator e = ElevatorController.getInstance().getElevatorById(selectedElevatorId);
        EventLogger.print("Selected Elevator " + e.getElevatorId());
        Direction nowGoingInDirection = (e.getLocation() < toFloorNumber) ? Direction.UP : Direction.DOWN;
        if((e.getLocation() > toFloorNumber && e.getDirection() == Direction.UP) ||
                (e.getLocation() < toFloorNumber && e.getDirection() == Direction.DOWN)) {
            e = ElevatorController.getInstance().getElevatorById(secondElevatorId);

        }
        EventLogger.print("E-" + e.getElevatorId() + " now going " + nowGoingInDirection.toString());
        e.setDirection(nowGoingInDirection);
        EventLogger.print("E-" + e.getElevatorId() + " next stop added to queue: (F-" + toFloorNumber + ").");
        e.addNextStop(toFloorNumber);
        if(!e.isDispatched()) {
            EventLogger.print("E-" + e.getElevatorId() + " dispatched to F-" + toFloorNumber + " to serve " + desiredDirection);
            e.setDispatched(true);
            e.setDispatchedForFloor(toFloorNumber);
            e.setDispatchedToServeDirection(desiredDirection);
        }
        if(e.getLocation() == toFloorNumber) {
            EventLogger.print("E-" + e.getElevatorId() + " was already at F-" + toFloorNumber);
            executeLocationUpdate(e.getElevatorId(), toFloorNumber, desiredDirection, e.getDispatchedToServeDirection());
        }
        e.move();
    }

    public void saveFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        HashMap<Integer, Direction> table = getFloorRequests();
        if(table.get(fromFloorNumber) != direction) {
            table.put(fromFloorNumber, direction);
            EventLogger.print("Floor Request (" + fromFloorNumber + ", " + direction + ") saved.");
        }
    }

    public void removeFloorRequest(int fromFloorNumber, Direction direction, int elevatorBoardedOn) throws ElevatorSystemException {
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

}
