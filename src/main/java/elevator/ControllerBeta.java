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
            final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
            this.floorRequestQueue = new ArrayBlockingQueue<>(2 * NUMBER_OF_FLOORS - 2);

            this.elevators = new HashMap<>();
            for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                Elevator e = new Elevator();
                this.elevators.put(i, e);
            }
        } catch (NumberFormatException nfe) {
            throw new ElevatorSystemException("Bad format in number of elevators. Check config file.");
        }
    }

    @Override
    public void run() throws ElevatorSystemException {
        Building building = Building.getInstance();
        int numberOfElevators = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        Thread threads[] = new Thread[numberOfElevators];
        for(int i = 1; i <= numberOfElevators; i++) {
            Elevator e = getElevator(i);
            threads[i - 1] = new Thread(() -> e.run());
            threads[i - 1].setName("THREAD_ELEVATOR_" + i);
        }
        Thread buildingThread = new Thread(() -> building.run());
        buildingThread.setName("THREAD_BUILDING");

        for(int i = 1; i <= numberOfElevators; i++) {
            threads[i - 1].start();
        }
        buildingThread.start();

        try {
            for(int i = 1; i <= numberOfElevators; i++) {
                threads[i - 1].join();
            }
            buildingThread.join();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(elevatorId, elevatorLocation, direction, directionDispatchedFor);
    }


    @Override
    public void executeElevatorRequest(int elevatorId, int personId, int destinationFloor, int fromFloorNumber) throws ElevatorSystemException {
        Elevator e = getElevator(elevatorId);
        e.enterRider(personId, destinationFloor);
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
        Elevator e = selectElevator1(fromFloorNumber, direction);
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
        announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    @Override
    public void exitRider(int elevatorId, int personId, int floorNumber) throws ElevatorSystemException {
        Elevator e = getElevator(elevatorId);
        e.exitRider(personId, floorNumber);

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

    private Elevator selectElevator1(int floor, Direction direction) {
        serviceCount++;
        int selected = 1 + serviceCount % 4;
        return getElevator(selected);
    }
    private Elevator selectElevator(int floor, Direction direction) {
        final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
            Elevator e = getElevator(i);
            if(e.getDirection() == Direction.IDLE) {
                return e;
            }
            if(e.getDirection() == Direction.UP && direction == Direction.UP && e.getLocation() <= floor) {
                return e;
            }
            if(e.getDirection() == Direction.DOWN && direction == Direction.DOWN && e.getLocation() >= floor) {
                return e;
            }
        }
        return null;//getElevator(1 + serviceCount % 4);
    }
}
