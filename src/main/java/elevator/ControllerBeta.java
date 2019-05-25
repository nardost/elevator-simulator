package elevator;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

class ControllerBeta implements Controller {

    private AbstractQueue<FloorRequest> floorRequestQueue;
    private Map<Integer, Elevator> elevators;

    private final int NUMBER_OF_FLOORS;
    private final int NUMBER_OF_ELEVATORS;

    private int serviceCount = 0;

    ControllerBeta() throws ElevatorSystemException {
        try {
            NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            NUMBER_OF_ELEVATORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
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
        Thread threads[] = new Thread[NUMBER_OF_ELEVATORS];
        for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
            Elevator e = getElevator(i);
            threads[i - 1] = new Thread(() -> e.run());
            threads[i - 1].setName("THREAD_ELEVATOR_" + i);
            threads[i - 1].start();
        }
        Thread buildingThread = new Thread(() -> building.run());
        buildingThread.setName("THREAD_BUILDING");
        buildingThread.start();
/*
        ControllerBeta self = this;
        Thread controllerThread = new Thread(() -> self.serveFloorRequests());
        controllerThread.setName("THREAD_CONTROLLER");
        controllerThread.start();
*/
        try {
            for(int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
                threads[i - 1].join();
            }
            //controllerThread.join();
            buildingThread.join();

        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    //TODO: trying another thread....
    private void serveFloorRequests() {
        System.out.println("inside controller thread...");
        while(true) {
            if(getFloorRequests().isEmpty()) {
                break;
            }
            System.out.println("*************************************************************");
            FloorRequest request = getFloorRequests().element();

            int fromFloorNumber = request.getFloorOfOrigin();
            Direction direction = request.getDirectionRequested();
            try {
                Elevator e = selectElevator(fromFloorNumber, direction);
                if (e != null) {
                    removeFloorRequest(request);
                    e.addFloorRequest(fromFloorNumber, direction);
                    e.setDispatched(true);
                    e.setDispatchedToServeDirection(direction);
                    e.setDispatchedForFloor(fromFloorNumber);
                    Thread.sleep(1000);
                }
            } catch(ElevatorSystemException ese) {
                //TODO
            } catch(InterruptedException ie) {
                //TODO
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
        saveFloorRequest(request);
        System.out.println("Running in thread...." + Thread.currentThread().getName());
        Elevator e = selectElevator(fromFloorNumber, direction);
        if(e != null) {
            removeFloorRequest(request);
            e.addFloorRequest(fromFloorNumber, direction);
            e.setDispatched(true);
            e.setDispatchedToServeDirection(direction);
            e.setDispatchedForFloor(fromFloorNumber);
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

    private Map<Integer, Elevator> getElevators() {
        return this.elevators;
    }

    private Elevator getElevator(int id) {
        return getElevators().get(new Integer(id));
    }

    private AbstractQueue<FloorRequest> getFloorRequests() {
        return this.floorRequestQueue;
    }
    private void saveFloorRequest(FloorRequest floorRequest) {
        AbstractQueue<FloorRequest> floorRequests = getFloorRequests();
        synchronized(floorRequests) {
            if(!floorRequests.contains(floorRequest)) {
                floorRequests.offer(floorRequest);
            }
        }
    }

    private void removeFloorRequest(FloorRequest floorRequest) {
        AbstractQueue<FloorRequest> floorRequests = getFloorRequests();
        synchronized(floorRequests) {
            floorRequests.remove(floorRequest);
        }
    }
    private String printListOfFloorRequests() throws ElevatorSystemException {
        List<FloorRequest> list = getFloorRequests().stream().collect(Collectors.toList());
        return Utility.listToString(list, "", ", ", "");
    }

    private Elevator selectElevator1(int floor, Direction direction) {
        serviceCount++;
        int selected = 1 + serviceCount % 4;
        return getElevator(selected);
    }
    private Elevator selectElevator2(int floor, Direction direction) {
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

        Random random = new Random(100);
        return getElevator(1 + random.nextInt(NUMBER_OF_ELEVATORS));
    }

    private Elevator selectElevator(int floor, Direction direction) throws ElevatorSystemException {
        Elevator e = null;
        if(elevator(floor) > 0) {
            //(1)
            if(elevator(floor) > 0 && (elevator(Direction.IDLE) > 0 || elevator(direction) > 0)) {//(5)
                //TODO
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
                    //TODO: PendingRequest
                    EventLogger.print("Pending Requests: " + printListOfFloorRequests());
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
