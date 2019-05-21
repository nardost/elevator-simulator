package elevator;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

class ControllerBeta implements Controller {

    private AbstractQueue<FloorRequest> floorRequestQueue;
    private final int NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
    private final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));

    private int serviceCount = 0;

    ControllerBeta() {
        this.floorRequestQueue = new ArrayBlockingQueue<>(2 * NUMBER_OF_FLOORS - 2);
    }

    @Override
    public void run() {
        while(getFloorRequests().peek() != null) {
        }
    }

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(elevatorId, elevatorLocation, direction, directionDispatchedFor);
    }


    @Override
    public void executeElevatorRequest(int elevatorId, int destinationFloor, int fromFloorNumber) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        Direction direction = (fromFloorNumber < destinationFloor) ? Direction.UP : Direction.DOWN;
        FloorRequest request = (FloorRequest) FloorRequestFlyweightFactory.getInstance()
                .getFloorRequest(Utility.encodeFloorRequestKey(fromFloorNumber, direction));
        //removeFloorRequest(request);
        e.addNextStop(destinationFloor);
        EventLogger.print(
                "Elevator " + e.getElevatorId() + " Rider Request made for Floor " + destinationFloor +
                        " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests " + e.printListOfRiderRequests() + "]");
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        FloorRequest request = (FloorRequest) FloorRequestFlyweightFactory.getInstance()
                .getFloorRequest(Utility.encodeFloorRequestKey(fromFloorNumber, direction));
        saveFloorRequest(request);
        Elevator e = selectElevator(fromFloorNumber, direction);
        if(e != null) {
            removeFloorRequest(request);
            e.setDispatched(true);
            e.setDispatchedToServeDirection(direction);
            e.setDispatchedForFloor(fromFloorNumber);
            e.addNextStop(fromFloorNumber);
        }
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        if(e.peekNextStop() != null) {/*
            EventLogger.print(
                    "Elevator " + elevatorId + " moving from Floor " + elevatorLocation + " to Floor " + e.peekNextStop() +
                            " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
        */}
        announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    AbstractQueue<FloorRequest> getFloorRequests() {
        return this.floorRequestQueue;
    }
    void saveFloorRequest(FloorRequest floorRequest) throws ElevatorSystemException {
        AbstractQueue<FloorRequest> floorRequests = getFloorRequests();
        synchronized(floorRequests) {
            if(!floorRequests.contains(floorRequest)) {
                floorRequests.offer(floorRequest);
            }
        }
    }

    void removeFloorRequest(FloorRequest floorRequest) throws ElevatorSystemException {
        AbstractQueue<FloorRequest> floorRequests = getFloorRequests();
        synchronized(floorRequests) {
            floorRequests.remove(floorRequest);
        }
    }
    String printListOfFloorRequests() {
        List<FloorRequest> list = getFloorRequests().stream().collect(Collectors.toList());
        return Utility.listToString(list, "", ", ", "");
    }

    private Elevator selectElevator(int floor, Direction direction) throws ElevatorSystemException {
        serviceCount++;
        int selected = 1 + serviceCount % 4;
        System.out.println("SELECTED " + selected);
        return ElevatorController.getInstance().getElevatorById(selected);/**
        Elevator elevators[] = new Elevator[NUMBER_OF_ELEVATORS];
        for(int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
            Elevator e = ElevatorController.getInstance().getElevatorById(i + 1);
            elevators[i] = e;
        }
        for(int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
            Elevator e = elevators[i];
            if(e.getDirection() == Direction.IDLE) {
                return e;
            }
            if(e.getDirection() == Direction.UP && direction == Direction.UP && e.getLocation() < floor) {
                return e;
            }
            if(e.getDirection() == Direction.DOWN && direction == Direction.DOWN && e.getLocation() > floor) {
                return e;
            }
        }
        return null;*/
    }
}
