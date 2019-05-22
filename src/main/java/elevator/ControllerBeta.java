package elevator;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

class ControllerBeta implements Controller {

    private AbstractQueue<FloorRequest> floorRequestQueue;
    private Map<Integer, Elevator> elevators;

    private int serviceCount = 0;

    ControllerBeta() throws ElevatorSystemException {
        try {
            final int NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            this.floorRequestQueue = new ArrayBlockingQueue<>(2 * NUMBER_OF_FLOORS - 2);
        } catch (NumberFormatException nfe) {
            throw new ElevatorSystemException("Bad format in number of elevators. Check config file.");
        }
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
        Elevator e = getElevator(elevatorId);
        e.addNextStop(destinationFloor);
        EventLogger.print(
                "Elevator " + e.getElevatorId() + " Rider Request made for Floor " + destinationFloor +
                        " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests " + e.printListOfRiderRequests() + "]");
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        /**
         * Load elevators in private Map. Making sure this gets done only on the first floor request.
         * Cannot do this in constructor because ElevatorController is not fully constructed yet.
         * This should be only temporary. Elevators should solely be owned by this controller.
         * */
        if(getElevators() == null) {
            synchronized(ControllerBeta.class) {
                if(getElevators() == null) {
                    this.elevators = new HashMap<>();
                    final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
                    for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                        Elevator e = ElevatorController.getInstance().getElevatorById(i);
                        this.elevators.put(new Integer(i), e);
                    }
                }
            }
        }
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
        Elevator e = getElevator(elevatorId);
        if(e.peekNextStop() != null) {/*
            EventLogger.print(
                    "Elevator " + elevatorId + " moving from Floor " + elevatorLocation + " to Floor " + e.peekNextStop() +
                            " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
        */}
        announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    private Map<Integer, Elevator> getElevators() {
        return this.elevators;
    }

    private Elevator getElevator(int id) {
        return getElevators().get(new Integer(id));
    }

    AbstractQueue<FloorRequest> getFloorRequests() {
        return this.floorRequestQueue;
    }
    void saveFloorRequest(FloorRequest floorRequest) {
        AbstractQueue<FloorRequest> floorRequests = getFloorRequests();
        synchronized(floorRequests) {
            if(!floorRequests.contains(floorRequest)) {
                floorRequests.offer(floorRequest);
            }
        }
    }

    void removeFloorRequest(FloorRequest floorRequest) {
        AbstractQueue<FloorRequest> floorRequests = getFloorRequests();
        synchronized(floorRequests) {
            floorRequests.remove(floorRequest);
        }
    }
    String printListOfFloorRequests() {
        List<FloorRequest> list = getFloorRequests().stream().collect(Collectors.toList());
        return Utility.listToString(list, "", ", ", "");
    }

    private Elevator selectElevator(int floor, Direction direction) {
        serviceCount++;
        int selected = 1 + serviceCount % 4;
        System.out.println("SELECTED " + selected);
        return getElevator(selected);
        /**
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
