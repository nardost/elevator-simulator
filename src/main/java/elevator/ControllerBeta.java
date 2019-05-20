package elevator;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

class ControllerBeta implements Controller {

    private Map<Integer, Direction> floorRequests = Collections.synchronizedMap(new HashMap<>());
    private List<FloorRequest> floorRequestFlyweights = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    @Override
    public void run() {
        Iterator iterator = getFloorRequestFlyweights().iterator();
        System.out.println("just outside loop in controller thread.....");
        while(iterator.hasNext()) {
            FloorRequest floorRequest = (FloorRequest) iterator.next();
            int fromFloorNumber = floorRequest.getFloorOfOrigin();
            Direction direction = floorRequest.getDirectionRequested();
            try {
                EventLogger.print("In controller thread............");

                Elevator e = selectElevator(fromFloorNumber, direction);
                if (e != null) {
                    e.setDispatched(true);
                    e.setDispatchedToServeDirection(direction);

                    e.setDispatchedForFloor(fromFloorNumber);

                    e.addNextStop(fromFloorNumber);

                    Thread.sleep(500L);
                }
            } catch(ElevatorSystemException ese) {
                ese.getMessage();
            } catch(InterruptedException ie) {
                ie.getMessage();
            }
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
        removeFloorRequest(fromFloorNumber, direction);
        //e.addNextStop(destinationFloor);
        EventLogger.print(
                "Elevator " + e.getElevatorId() + " Rider Request made for Floor " + destinationFloor +
                        " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests " + e.printListOfRiderRequests() + "]");
        //e.run();
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        saveFloorRequest(fromFloorNumber, direction);/**
        Elevator e = selectElevator(fromFloorNumber, direction);
        if(e != null) {
            e.setDispatched(true);
            e.setDispatchedToServeDirection(direction);
            e.setDispatchedForFloor(fromFloorNumber);
            e.addNextStop(fromFloorNumber);
            //e.run();
        }*/
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {
        Elevator e = ElevatorController.getInstance().getElevatorById(elevatorId);
        if(e.peekNextStop() != null) {
            EventLogger.print(
                    "Elevator " + elevatorId + " moving from Floor " + elevatorLocation + " to Floor " + e.peekNextStop() +
                            " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + e.printListOfRiderRequests() + "]");
        }
        announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    private Map<Integer, Direction> getFloorRequests() {
        return floorRequests;
    }
    private List<FloorRequest> getFloorRequestFlyweights() {
        return this.floorRequestFlyweights;
    }
    public void saveFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        Map<Integer, Direction> table = getFloorRequests();
        List<FloorRequest> list = getFloorRequestFlyweights();
        FloorRequest flyweight = (FloorRequest) FloorRequestFlyweightFactory.getInstance().getFloorRequest(Utility.encodeFloorRequestKey(fromFloorNumber, direction));
        if(table.get(fromFloorNumber) != direction) {
            table.put(fromFloorNumber, direction);
            list.add(flyweight);
            EventLogger.print(flyweight.toString() + " saved.");
        }
    }

    public void removeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        Map<Integer, Direction> table = getFloorRequests();
        List<FloorRequest> list = getFloorRequestFlyweights();
        FloorRequestFlyweight flyweight = FloorRequestFlyweightFactory.getInstance().getFloorRequest(Utility.encodeFloorRequestKey(fromFloorNumber, direction));
        if(table.containsKey(fromFloorNumber)) {
            table.remove(fromFloorNumber, direction);
            list.remove(flyweight);
            EventLogger.print(flyweight.toString() + " removed.");
        }
    }

    String printListOfFloorRequests() {
        List<Integer> list = new ArrayList<>(getFloorRequests().keySet());
        return Utility.listToString(list, "", ", ", "");
    }

    private Elevator selectElevator(int floor, Direction direction) throws ElevatorSystemException {
        Elevator selectedElevator = null;
        int numberOfElevators = Building.getInstance().getNumberOfElevators();
        Elevator elevators[] = new Elevator[numberOfElevators];
        for(int i = 0; i < numberOfElevators; i++) {
            Elevator e = ElevatorController.getInstance().getElevatorById(i + 1);
            elevators[i] = e;
        }
        for(int i = 0; i < numberOfElevators; i++) {
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
        return selectedElevator;
    }
}
