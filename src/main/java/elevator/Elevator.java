package elevator;

import gui.ElevatorDisplay;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

import static gui.ElevatorDisplay.Direction.DOWN;
import static gui.ElevatorDisplay.Direction.UP;

class Elevator implements GenericElevator {

    private int elevatorId;
    private int speed;
    private int location;
    private Direction direction;
    private boolean dispatched;
    private int dispatchedForFloor; //U-turn point
    private Direction dispatchedToServeDirection;
    private boolean doorsClosed;

    private List<Integer> riderRequests = new ArrayList<>();
    private List<Integer> riders = new ArrayList<>();

    private PriorityBlockingQueue<Integer> nextFloorQueueNatural = new PriorityBlockingQueue<>(20, Comparator.naturalOrder());
    private PriorityBlockingQueue<Integer> nextFloorQueueReverse = new PriorityBlockingQueue<>(20, Comparator.reverseOrder());

    private static int instanceCounter = 0;

    Elevator() throws ElevatorSystemException {
        SystemConfiguration.initializeSystemConfiguration();//TODO: Important!!!
        setElevatorId(++instanceCounter);
        setSpeed();
        setDirection(Direction.IDLE);
        setDoorsClosed(true);
        try {
            setLocation(Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor")));
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for default floor.");
        }
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof Elevator) {
            if (getElevatorId() == ((Elevator) object).getElevatorId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElevatorId());
    }

    public int getSpeed() {
        return this.speed;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public int getLocation() {
        return this.location;
    }

    @Override
    public void move() throws ElevatorSystemException {
        //closeDoors();
        long floorTime = 1000L * (long) getSpeed();
        int floor = peekNextStop();
        if(noMoreStops()) {
            if(getLocation() != Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor"))) {
                setIdle();
            }
            EventLogger.print("Elevator " + getElevatorId() + " has accomplished its mission.");
            return;
        }

        if(getLocation() < floor) {
            for (int i = getLocation(); i <= floor; i++) {
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), UP);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
                }
                setLocation(i);
                Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());

                if(Building.TEST == 2 && i == 5 && Building.getInstance().getNumberOfPeopleGenerated() == 1) {
                    Building.getInstance().generatePerson(15, 19);
                    i = getLocation();
                }
                if(Building.TEST == 4) {
                    if(i == 5 && Building.getInstance().getNumberOfPeopleGenerated() == 1) {
                        Building.getInstance().generatePerson(8, 17);
                        i = getLocation();
                    }
                    if(i == 7 && Building.getInstance().getNumberOfPeopleGenerated() == 2) {
                        //Elevator 4 should respond to this.
                        Building.getInstance().generatePerson(1, 9);
                        i = getLocation();
                    }
                }
            }
        } else {
            for (int i = getLocation(); i >= floor; i--) {
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), DOWN);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
                }
                setLocation(i);
                Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());
                if(Building.TEST == 3 && i == 15 && Building.getInstance().getNumberOfPeopleGenerated() == 1) {
                    Building.getInstance().generatePerson(10, 1);
                    i = getLocation();
                }
                if(Building.TEST == 4) {
                    if(i == 5 && Building.getInstance().getNumberOfPeopleGenerated() == 3) {
                        //Elevator 4 should respond to this.
                        Building.getInstance().generatePerson(3, 1);
                        i = getLocation();
                    }
                }
            }
        }
    }

    @Override
    public void stop() throws ElevatorSystemException {
        openDoors();
        //TODO: EventLogger.print("Print why elevator is stopping.");
        closeDoors();
    }

    private void openDoors() throws ElevatorSystemException {
        setDoorsClosed(false);
        EventLogger.print("Elevator " + getElevatorId() + " Doors Open");
        try {
            long doorTime = Long.parseLong(SystemConfiguration.getConfiguration("doorTime"));
            ElevatorDisplay.getInstance().openDoors(getElevatorId());
            Thread.sleep(doorTime * 1000L);
        } catch(InterruptedException ie) {
            throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for door time");
        }
    }

    private void closeDoors() throws ElevatorSystemException {
        setDoorsClosed(true);
        EventLogger.print("Elevator " + getElevatorId() + " Doors Close");
        ElevatorDisplay.getInstance().closeDoors(getElevatorId());
    }

    void enterRider(int personId, int destinationFloor) throws ElevatorSystemException {
        addRiderRequest(destinationFloor);
        addRider(personId);
        EventLogger.print("Person P" + personId + " has entered Elevator " + getElevatorId() + " [Riders: " + printListOfRiders() + "]");

    }
    void exitRider(int personId, int floorNumber) throws ElevatorSystemException {
        removeRiderRequest(floorNumber);
        removeRider(personId);
        EventLogger.print("Person P" + personId + " has left Elevator " + getElevatorId() + " [Riders: " + printListOfRiders() + "]");
    }

    void addRiderRequest(int destinationFloor) {
        if(!getRiderRequests().contains(destinationFloor)) {
            getRiderRequests().add(destinationFloor);
        }
    }
    void removeRiderRequest(int floorNumber) {
        Integer o = new Integer(floorNumber);
        if(getRiderRequests().contains(o)) {
            getRiderRequests().remove(o);
        }
    }

    void addRider(int personId) throws ElevatorSystemException {
        if(!getRiders().contains(personId)) {
            int maxCapacity = Integer.parseInt(SystemConfiguration.getConfiguration("elevatorCapacity"));
            int numberOfRiders = getNumberOfRiders();
            if(numberOfRiders == maxCapacity) {
                throw new ElevatorSystemException("The maximum elevator capacity is " + maxCapacity + " riders only.");
            }
            getRiders().add(personId);
        }
    }
    void removeRider(int personId) {
        Integer o = new Integer(personId);
        if(getRiders().contains(o)) {
            getRiders().remove(o);
        }
    }

    private List<Integer> getRiders() {
        return riders;
    }

    private List<Integer> getRiderRequests() {
        return this.riderRequests;
    }

    List<Integer> getCopyOfListOfRiders() {
        List<Integer> copyOfRiders = getRiderRequests().stream().collect(Collectors.toList());
        return copyOfRiders;
    }

    int getElevatorId() {
        return elevatorId;
    }

    PriorityBlockingQueue<Integer> getNextFloorQueue() {
        if(getDirection() == Direction.UP) {
            return nextFloorQueueNatural;
        }
        return nextFloorQueueReverse;
    }

    int getNumberOfRiders() {
        if(getRiders().isEmpty()) {
            return 0;
        }
        return getRiders().size();
    }

    private void setElevatorId(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    private void setSpeed() throws ElevatorSystemException {
        try {
            this.speed = Integer.parseInt(SystemConfiguration.getConfiguration("floorTime"));
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for floor time.");
        }
    }

    void setLocation(int location) throws ElevatorSystemException {
        //NOTE: I used Building.getInstance().getNumberOfFloors() and got stuck for hours.
        if(location < 1 || location > Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"))) {
            throw new ElevatorSystemException("Floors can only be 1 to " + Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors")));
        }
        this.location = location;
    }

    void setIdle() throws ElevatorSystemException {

        EventLogger.print("Elevator " + getElevatorId() + " idling it out at Floor " + getLocation());

        try {
            Thread.sleep(Long.parseLong(SystemConfiguration.getConfiguration("timeout")) * 1000L);
        } catch(InterruptedException ie) {
            throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
        }
    }

    void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void setDoorsClosed(boolean doorsClosed) {
        this.doorsClosed = doorsClosed;
    }

    boolean isDispatched() {
        return dispatched;
    }

    void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    int getDispatchedForFloor() {
        return dispatchedForFloor;
    }

    Direction getDispatchedToServeDirection() {
        return dispatchedToServeDirection;
    }

    void setDispatchedToServeDirection(Direction dispatchedToServeDirection) {
        this.dispatchedToServeDirection = dispatchedToServeDirection;
    }

    void setDispatchedForFloor(int dispatchedForFloor) throws ElevatorSystemException {
        int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        if(dispatchedForFloor < 0 || dispatchedForFloor > numberOfFloors) {
            throw new ElevatorSystemException("Floors between 1 and " + numberOfFloors);
        }
        this.dispatchedForFloor = dispatchedForFloor;
    }

    void addNextStop(int next) {
        if(!getNextFloorQueue().contains(next)) {
            getNextFloorQueue().offer(next);
        }
    }

    Integer peekNextStop() {
        if(getNextFloorQueue().peek() != null) {
            return getNextFloorQueue().peek();
        }
        return null;
    }
    Integer pollNextStop() {
        if(peekNextStop() != null) {
            int next = getNextFloorQueue().poll();
            return next;
        }
        return null;
    }
    boolean noMoreStops() {
        if(peekNextStop() == null) {
            return true;
        }
        return false;
    }

    String printListOfRiderRequests() {
        return Utility.listToString(getRiderRequests(), "", ", ", "");
    }

    String printListOfRiders() {
        return Utility.listToString(getRiders(), "P", ", ", "");
    }

}
