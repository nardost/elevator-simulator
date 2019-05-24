package elevator;

import gui.ElevatorDisplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static gui.ElevatorDisplay.Direction.*;

class Elevator implements GenericElevator {

    private int elevatorId;
    private int speed;
    private int location;
    private Direction direction;
    private boolean dispatched;
    private int dispatchedForFloor; //U-turn point. Currently unused.
    private Direction dispatchedToServeDirection;
    private boolean doorsOpen;
    private static int defaultFloor;

    private List<Integer> riderRequests = new ArrayList<>();
    private List<Integer> floorRequests = new ArrayList<>();
    private List<Integer> riders = new ArrayList<>();

    private PriorityBlockingQueue<Integer> nextFloorQueueNatural = new PriorityBlockingQueue<>(20, Comparator.naturalOrder());
    private PriorityBlockingQueue<Integer> nextFloorQueueReverse = new PriorityBlockingQueue<>(20, Comparator.reverseOrder());

    private static int instanceCounter = 0;

    Elevator() throws ElevatorSystemException {
        SystemConfiguration.initializeSystemConfiguration();
        setElevatorId(++instanceCounter);
        setSpeed();
        setDirection(Direction.IDLE);
        try {
            setDefaultFloor(Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor")));
            setLocation(getDefaultFloor());
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for default floor.");
        }
    }

    void run() {
        try {
            long elapsedSeconds = TimeUnit.SECONDS.convert((System.nanoTime() - Building.getInstance().getZeroTime()), TimeUnit.NANOSECONDS);
            final long SIMULATION_DURATION = Long.parseLong(SystemConfiguration.getConfiguration("simulationDuration"));
            while (elapsedSeconds < SIMULATION_DURATION * 5L) {

                if (nextStop()) {
                    move();
                }

                if (getLocation() != getDefaultFloor()) {
                    ElevatorDisplay.getInstance().updateElevator(getElevatorId(), getLocation(), getNumberOfRiders(), DOWN);
                    closeDoors();
                    setIdle();
                    addNextStop(getDefaultFloor());
                    setDirection(Utility.evaluateDirection(getLocation(), getDefaultFloor()));
                    move();
                    setDirection(Direction.IDLE);
                }

                if(doorsOpen()) {
                    closeDoors();
                }

                Thread.sleep(500L);
                elapsedSeconds = TimeUnit.SECONDS.convert((System.nanoTime() - Building.getInstance().getZeroTime()), TimeUnit.NANOSECONDS);
            }
        } catch(ElevatorSystemException ese) {
            ese.getMessage();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
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

        EventLogger.print(
                "Elevator " + elevatorId + " moving from Floor " + getLocation() + " to Floor " + peekNextStop() +
                        " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + printListOfRiderRequests() + "]");
        long floorTime = 1000L * (long) getSpeed();
        int floor = pollNextStop();
        if(doorsOpen()) {
            closeDoors();
        }
        if(floor == getLocation()) {
            openDoors();
            Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());
            ElevatorDisplay.getInstance().updateElevator(getElevatorId(), getLocation(), getNumberOfRiders(), DOWN);
            return;
        }

        if(getLocation() < floor) {
            for (int i = getLocation(); i <= floor; i++) {
                setDirection(Direction.UP);
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), UP);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
                }
                setLocation(i);
                if(floor == getLocation()) {
                    openDoors();
                }
                Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());
            }
            ElevatorDisplay.getInstance().updateElevator(getElevatorId(), getLocation(), getNumberOfRiders(), IDLE);
        } else {
            for (int i = getLocation(); i >= floor; i--) {
                setDirection(Direction.DOWN);
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), DOWN);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
                }
                setLocation(i);
                if(floor == getLocation()) {
                    openDoors();
                }
                Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());
            }
            ElevatorDisplay.getInstance().updateElevator(getElevatorId(), getLocation(), getNumberOfRiders(), IDLE);
        }
    }

    private void openDoors() throws ElevatorSystemException {
        this.doorsOpen = true;
        EventLogger.print("Elevator " + getElevatorId() + " Doors Open...");
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
        setDoorsOpen(false);
        EventLogger.print("Elevator " + getElevatorId() + " Doors Close...");
        ElevatorDisplay.getInstance().closeDoors(getElevatorId());
    }

    boolean doorsOpen() {
        return this.doorsOpen;
    }

    private void setDoorsOpen(boolean open) {
        this.doorsOpen = open;
    }

    void enterRider(int personId, int destinationFloor) throws ElevatorSystemException {
        Validator.validateGreaterThanZero(personId);
        Validator.validateFloorNumber(destinationFloor);
        addRiderRequest(destinationFloor);
        addRider(personId);
        EventLogger.print("Person P" + personId + " has entered Elevator " + getElevatorId() + " [Riders: " + printListOfRiders() + "]");

    }
    void exitRider(int personId, int floorNumber) throws ElevatorSystemException {
        Validator.validateGreaterThanZero(personId);
        Validator.validateFloorNumber(floorNumber);
        removeRiderRequest(floorNumber);
        removeRider(personId);
        EventLogger.print("Person P" + personId + " has left Elevator " + getElevatorId() + " [Riders: " + printListOfRiders() + "]");
    }

    private void addRiderRequest(int destinationFloor) throws ElevatorSystemException {
        Validator.validateFloorNumber(destinationFloor);
        Integer o = new Integer(destinationFloor);
        if(!getRiderRequests().contains(o)) {
            getRiderRequests().add(o);
            EventLogger.print(
                    "Elevator " + getElevatorId() + " Rider Request made for Floor " + destinationFloor +
                    " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + printListOfRiderRequests() + "]");
        }
    }
    private void removeRiderRequest(int floorNumber) throws ElevatorSystemException {
        Validator.validateFloorNumber(floorNumber);
        Integer o = new Integer(floorNumber);
        if(getRiderRequests().contains(o)) {
            getRiderRequests().remove(o);
        }
    }

    void addFloorRequest(int destinationFloor) throws ElevatorSystemException {
        Validator.validateFloorNumber(destinationFloor);
        Integer o = new Integer(destinationFloor);
        if(!getFloorRequests().contains(o)) {
            getFloorRequests().add(o);
        }
        EventLogger.print(
                "Elevator " + getElevatorId() + " Floor Request made for Floor " + destinationFloor +
                " [Current Floor Requests: " + printListOfFloorRequests() + "][Current Rider Requests: " + printListOfRiderRequests() + "]");
    }

    private void removeFloorRequest(int floorNumber) throws ElevatorSystemException {
        Validator.validateFloorNumber(floorNumber);
        Integer o = new Integer(floorNumber);
        if(getFloorRequests().contains(o)) {
            getFloorRequests().remove(o);
        }
    }

    void addRider(int personId) throws ElevatorSystemException {
        Validator.validateGreaterThanZero(personId);
        if(!getRiders().contains(personId)) {
            int maxCapacity = Integer.parseInt(SystemConfiguration.getConfiguration("elevatorCapacity"));
            int numberOfRiders = getNumberOfRiders();
            if(numberOfRiders == maxCapacity) {
                throw new ElevatorSystemException("The maximum elevator capacity is " + maxCapacity + " riders only.");
            }
            getRiders().add(personId);
        }
    }
    void removeRider(int personId) throws ElevatorSystemException {
        Validator.validateGreaterThanZero(personId);
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

    private List<Integer> getFloorRequests() {
        return this.floorRequests;
    }

    int getElevatorId() {
        return elevatorId;
    }

    PriorityBlockingQueue<Integer> getNextFloorQueue() {
        if(getDirection() == Direction.UP) {
            return nextFloorQueueNatural;
        }
        if(getDirection() == Direction.IDLE) {
            //TODO: return the down going queue... no logic here.
        }
        return nextFloorQueueReverse;
    }

    PriorityBlockingQueue<Integer> getNaturalNextFloorQueue() {
        return this.nextFloorQueueNatural;
    }

    PriorityBlockingQueue<Integer> getReverseNextFloorQueue() {
        return this.nextFloorQueueReverse;
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
        Validator.validateFloorNumber(location);
        this.location = location;
    }

    void setIdle() throws ElevatorSystemException {
        setDirection(Direction.IDLE);
        EventLogger.print("Elevator " + getElevatorId() + " going idle at Floor " + getLocation());

        try {
            Thread.sleep(Long.parseLong(SystemConfiguration.getConfiguration("timeout")) * 1000L);
        } catch(InterruptedException ie) {
            throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
        }
    }

    void setDirection(Direction direction) {
        this.direction = direction;
    }

    private static int getDefaultFloor() {
        return defaultFloor;
    }

    private static void setDefaultFloor(int defaultFloor) {
        Elevator.defaultFloor = defaultFloor;
    }

    boolean isDispatched() {
        return dispatched;
    }

    void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    int getDispatchedForFloor() {
        return this.dispatchedForFloor;
    }

    Direction getDispatchedToServeDirection() {
        return dispatchedToServeDirection;
    }

    void setDispatchedToServeDirection(Direction dispatchedToServeDirection) {
        this.dispatchedToServeDirection = dispatchedToServeDirection;
    }

    void setDispatchedForFloor(int dispatchedForFloor) throws ElevatorSystemException {
        Validator.validateFloorNumber(dispatchedForFloor);
        this.dispatchedForFloor = dispatchedForFloor;
    }

    void addNextStop(int next) throws ElevatorSystemException {
        Validator.validateFloorNumber(next);
        if(next < getLocation()) {
            if(!getReverseNextFloorQueue().contains(next)) {
                getReverseNextFloorQueue().offer(next);
            }
            return;
        }
        if(!getNaturalNextFloorQueue().contains(next)) {
            getNaturalNextFloorQueue().offer(next);
        }

    }

    private Integer peekNextStop() {
        if(getDirection() == Direction.IDLE) {
            if(getNaturalNextFloorQueue().peek() != null) {
                return getNaturalNextFloorQueue().peek();
            }
            return getReverseNextFloorQueue().peek();
        }
        if(getNextFloorQueue().peek() != null) {
            return getNextFloorQueue().peek();
        }
        return null;
    }
    private Integer pollNextStop() throws ElevatorSystemException {
        if(getDirection() == Direction.IDLE) {
            if(getNaturalNextFloorQueue().peek() != null) {
                int next = getNaturalNextFloorQueue().poll();
                if(getFloorRequests().contains(new Integer(next))) {
                    removeFloorRequest(new Integer(next));
                }
                if(getRiderRequests().contains(next)) {
                    removeRiderRequest(next);
                }
                return next;
            }
            return getReverseNextFloorQueue().poll();
        }
        if(getNextFloorQueue().peek() != null) {
            return getNextFloorQueue().poll();
        }
        return null;
    }
    private boolean nextStop() {
        if(getReverseNextFloorQueue().peek() == null && getNaturalNextFloorQueue().peek() == null) {
            return false;
        }
        return true;
    }

    String printListOfRiderRequests() throws ElevatorSystemException {
        return Utility.listToString(getRiderRequests(), "", ", ", "");
    }

    String printListOfRiders() throws ElevatorSystemException {
        return Utility.listToString(getRiders(), "P", ", ", "");
    }
    String printListOfFloorRequests() throws ElevatorSystemException {
        return Utility.listToString(getFloorRequests(), "", ", ", "");
    }
}
