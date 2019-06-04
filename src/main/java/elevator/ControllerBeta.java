package elevator;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

class ControllerBeta implements Controller {

    private ArrayBlockingQueue<FloorRequest> floorRequestQueue;
    private ArrayBlockingQueue<FloorRequest> pendingRequestQueue;
    private Map<Integer, Elevator> elevators;

    private final int NUMBER_OF_FLOORS;
    private final int NUMBER_OF_ELEVATORS;

    ControllerBeta() throws ElevatorSystemException {
        try {
            NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));

            this.floorRequestQueue = new ArrayBlockingQueue<>(2 * NUMBER_OF_FLOORS - 2);
            this.pendingRequestQueue = new ArrayBlockingQueue<>(2 * NUMBER_OF_FLOORS - 2);

            this.elevators = new HashMap<>();
            for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                Elevator e = new Elevator();
                this.elevators.put(i, e);
            }
        } catch (NumberFormatException nfe) {
            throw new ElevatorSystemException("Bad number format in configuration file.");
        }
    }

    @Override
    public void run() throws ElevatorSystemException {
        Building building = Building.getInstance();
        Thread elevatorThreads[] = new Thread[NUMBER_OF_ELEVATORS];
        for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
            Elevator e = getElevator(i);
            elevatorThreads[i - 1] = new Thread(() -> e.run());
            elevatorThreads[i - 1].setName("elevator_" + i);
            elevatorThreads[i - 1].start();
        }
        Thread buildingThread = new Thread(() -> building.run());
        buildingThread.setName("  building");
        buildingThread.start();

        ControllerBeta self = this;
        Thread controllerThread = new Thread(() -> self.serveFloorRequests());
        controllerThread.setName("controller");
        controllerThread.start();

        try {
            for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                elevatorThreads[i - 1].join();
            }
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void serveFloorRequests() {
        boolean running = false;
        for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
            running = running || getElevator(i).isRunning();
        }
        while(running) {
            try {
                synchronized (getFloorRequests()) {
                    getFloorRequests().wait();
                }
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
            try {
                FloorRequest request = getFloorRequests().poll();
                if(request == null) {
                    continue;
                }

                Elevator e = selectElevator(request);
                if (e != null) {
                    int fromFloorNumber = request.getFloorOfOrigin();
                    Direction direction = request.getDirectionRequested();
                    EventLogger.print("Elevator " + e.getElevatorId() + " allocated to floor request " + request.toString());
                    e.addFloorRequest(fromFloorNumber, direction);
                    e.setDispatched(true);
                    e.setDispatchedToServeDirection(direction);
                    e.setDispatchedForFloor(fromFloorNumber);
                }
            } catch(ElevatorSystemException ese) {
                System.out.println(ese.getMessage());
            }
            running = false;
            for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                running = running || getElevator(i).isRunning();
            }
        }
    }

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        Validator.validateFloorNumber(elevatorLocation);
        Validator.validateElevatorNumber(elevatorId);
        Building.getInstance().notifyObservers(elevatorId, elevatorLocation, direction, directionDispatchedFor);
    }


    @Override
    public void executeElevatorRequest(int elevatorId, int personId, int destinationFloor, int fromFloorNumber) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateFloorNumber(destinationFloor);
        Validator.validateFloorNumber(fromFloorNumber);
        Validator.validateGreaterThanZero(personId);
        Elevator e = getElevator(elevatorId);
        e.enterRider(personId, destinationFloor);
        e.addRiderRequest(destinationFloor);
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        Validator.validateFloorNumber(fromFloorNumber);
        FloorRequest request = (FloorRequest) FloorRequestFlyweightFactory.getInstance()
                .getFloorRequest(Utility.encodeFloorRequestKey(fromFloorNumber, direction));

        synchronized(getFloorRequests()) {
            saveFloorRequest(request);
            getFloorRequests().notifyAll();
        }
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateFloorNumber(elevatorLocation);
        announceLocationOfElevator(elevatorId, elevatorLocation, nowGoingInDirection, directionDispatchedFor);
    }

    @Override
    public void exitRider(int elevatorId, int personId, int floorNumber) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateGreaterThanZero(personId);
        Validator.validateFloorNumber(floorNumber);
        Elevator e = getElevator(elevatorId);
        e.exitRider(personId, floorNumber);

    }

    @Override
    public boolean pendingFloorRequests(int elevatorId) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        //(1)
        if(getPendingRequests().isEmpty()) {
            //(2)
            return false;
        }
        //(3)
        Elevator e = getElevator(elevatorId);
        FloorRequest request = getPendingRequests().poll();
        int fromFloorNumber = request.getFloorOfOrigin();
        Direction direction = request.getDirectionRequested();
        e.addFloorRequest(fromFloorNumber, direction);
        e.setDispatched(true);
        e.setDispatchedToServeDirection(direction);
        e.setDispatchedForFloor(fromFloorNumber);
        //(4)
        while(!getPendingRequests().isEmpty()) {
            Iterator iterator = getPendingRequests().iterator();
            while(iterator.hasNext()) {
                //(6)
                request = (FloorRequest) iterator.next();
                if(direction == request.getDirectionRequested()) {
                    //(8)
                    if(direction == Direction.UP) {
                        //(9)
                        if(Utility.evaluateDirection(fromFloorNumber, request.getFloorOfOrigin()) == Direction.UP) {
                            //(11)
                            getPendingRequests().remove(request);
                            fromFloorNumber = request.getFloorOfOrigin();
                            direction = request.getDirectionRequested();
                            e.addFloorRequest(fromFloorNumber, direction);
                            continue;
                        }//(7)
                    } else {
                        //(10)
                        if(Utility.evaluateDirection(fromFloorNumber, request.getFloorOfOrigin()) == Direction.DOWN) {
                            //(12)
                            getPendingRequests().remove(request);
                            fromFloorNumber = request.getFloorOfOrigin();
                            direction = request.getDirectionRequested();
                            e.addFloorRequest(fromFloorNumber, direction);
                            continue;
                        } else {
                            //(13)
                            continue;
                        }
                    }
                } else {
                    //(7)
                    continue;
                }
            }
        }
        //(5)
        return true;
    }

    private Map<Integer, Elevator> getElevators() {
        return this.elevators;
    }

    private Elevator getElevator(int id) {
        return getElevators().get(new Integer(id));
    }

    private ArrayBlockingQueue<FloorRequest> getFloorRequests() {
        return this.floorRequestQueue;
    }
    private void saveFloorRequest(FloorRequest floorRequest) {
        ArrayBlockingQueue<FloorRequest> floorRequests = getFloorRequests();
        if(!floorRequests.contains(floorRequest)) {
            floorRequests.offer(floorRequest);
        }
    }

    private ArrayBlockingQueue<FloorRequest> getPendingRequests() {
        return this.pendingRequestQueue;
    }

    private String printListOfFloorRequests() throws ElevatorSystemException {
        List<FloorRequest> list = getPendingRequests().stream().collect(Collectors.toList());
        return Utility.listToString(list, "", ", ", "");
    }

    private Elevator selectElevator(FloorRequest request) throws ElevatorSystemException {
        int floor = request.getFloorOfOrigin();
        Direction direction = request.getDirectionRequested();
        Elevator e = null;
        if(elevator(floor) > 0) {
            //(1)
            if(elevator(floor) > 0 && (elevator(Direction.IDLE) > 0 || elevator(direction) > 0)) {//(5)
                for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                    e = getElevator(i);
                    if(e.getLocation() == floor && (e.getDirection() == Direction.IDLE || e.getDirection() == direction)) {
                        return e;
                    }
                }
            }//(6)
        } else {//(2)
            if(!(elevator(Direction.IDLE) > 0)) {
                //(7)
                boolean array[] = new boolean[NUMBER_OF_ELEVATORS];
                for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                    e = getElevator(i);
                    if(e.getLocation() != floor && e.getDirection() != Direction.IDLE) {
                        array[i - 1] = true;
                    }
                }
                e = findElevatorWithRightDirection(array, floor, direction);
                if(e != null) {//(8)
                    return e;
                }//(9)
            } else {//(3)
                if(elevator(Direction.IDLE) > 0) {
                    //(10)
                    for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                        e = getElevator(i);
                        if(e.getLocation() != floor && e.getDirection() == Direction.IDLE) {
                            return e;
                        }
                    }
                } else {//(4)
                    //Pending Request
                    getPendingRequests().offer(request);
                    return null;
                }
            }
        }
        return e;
    }

    private Elevator findElevatorWithRightDirection(boolean a[], int floor, Direction direction) throws ElevatorSystemException {

        for(int i = 1; i <= NUMBER_OF_ELEVATORS && a[i - 1]; i++) {
            Elevator e = getElevator(i);
            if(e.isServingARiderRequest()) {//(11)
                if(movingToward(e.getLocation(), e.getDirection(), floor)) {//(12)
                    if(e.getDirection() == direction) {//(13)
                        return e;
                    } else {//(15)
                        return null;
                    }
                } else {//(14)
                    return null;
                }
            } else {//(16)
                if(!e.isServingAFloorRequest()) {
                    throw new ElevatorSystemException("Elevator " + e.getElevatorId() + " should be serving a floor request.");
                }
                if(movingToward(e.getLocation(), e.getDirection(), floor)) {//(17)
                    if(e.getDirection() == direction) {//(18)
                        if(e.getDispatchedToServeDirection() == direction) {//(19)
                            return e;
                        } else {//(22)
                            return null;
                        }
                    } else {//(21)
                        return null;
                    }

                } else {//(20)
                    return null;
                }
            }
        }
        return null;
    }

    private int elevator(int floor) {
        final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
            if(getElevator(i).getLocation() == floor) {
                return i;
            }
        }
        return 0;
    }

    private int elevator(Direction direction) {
        final int NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
            if(getElevator(i).getDirection() == direction) {
                return i;
            }
        }
        return 0;
    }

    private boolean movingToward(int elevatorLocation, Direction elevatorDirection, int floor) {
        if(elevatorDirection == Direction.UP && elevatorLocation <= floor) return true;
        if(elevatorDirection == Direction.DOWN && elevatorLocation >= floor) return true;
        return false;
    }

    @Override
    public String unhandledFloorRequests() throws ElevatorSystemException {
        return printListOfFloorRequests();
    }
}
