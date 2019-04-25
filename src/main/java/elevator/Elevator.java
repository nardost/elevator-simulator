package elevator;

import gui.ElevatorDisplay;

import java.util.*;

import static gui.ElevatorDisplay.Direction.DOWN;
import static gui.ElevatorDisplay.Direction.UP;

enum Direction {IDLE, UP, DOWN}

class Elevator implements GenericElevator, Controllable, Observer {

    private int elevatorId;
    private int speed;
    private int location;
    private Direction direction;
    private boolean doorsClosed;

    private int numberOfRiders;

    private List<Observer> riders;

    private PriorityQueue<Integer> nextFloorQueue;

    private static int instanceCounter = 0;

    public Elevator() throws ElevatorSystemException {

        setElevatorId(++instanceCounter);
        setSpeed();
        setDirection(Direction.IDLE);
        setDoorsClosed(true);
        setRiders(new ArrayList<>());
        try {
            setLocation(Integer.parseInt(SystemConfiguration.getConfig("default-floor")));
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for default floor.");
        }

        setNextFloorQueue(new PriorityQueue<>(Comparator.naturalOrder()));//TODO: Collections.reverseOrder() dynamically on UP | DOWN
    }

    @Override
    public int getSpeed() {
        return this.speed;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public boolean areDoorsClosed() {
        return this.doorsClosed;
    }

    @Override
    public int getLocation() {
        return this.location;
    }

    @Override
    public void moveTo(int floor, Direction direction) throws ElevatorSystemException {

        closeDoors();

        setDirection(direction);

        if(floor == getLocation()) {
            System.out.println("Elevator is already on the same floor.");
            openDoors();
            return;
        }

        long floorTime = 1000L * (long) getSpeed();
        EventLogger.getInstance().logEvent(new Date() + " Elevator " + getElevatorId() + " moving to floor " + floor);
        if(getLocation() < floor) {
            for (int i = getLocation(); i <= floor; i++) {
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), UP);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
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
            }
        }

        openDoors();

        EventLogger.getInstance().logEvent(new Date() + " Elevator " + getElevatorId() + " at floor " + floor);

        setLocation(floor);
        Building.getInstance().relayNotificationToControlCenter(new Notification(getElevatorId(), getLocation(), getDirection()));
    }

    @Override
    public void openDoors() throws ElevatorSystemException {
        setDoorsClosed(false);
        try {
            long doorTime = Long.parseLong(SystemConfiguration.getConfig("door-time"));
            System.out.print(" : doors open");
            ElevatorDisplay.getInstance().openDoors(getElevatorId());
            Thread.sleep(doorTime * 1000L);
        } catch(InterruptedException ie) {
            throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for door time");
        }
    }

    @Override
    public void closeDoors() {
        setDoorsClosed(true);
        ElevatorDisplay.getInstance().closeDoors(getElevatorId());
        System.out.println(" : doors closed");
    }


    /**
     * Invoked when:
     *  - Person enters Elevator and requests to go to Floor
     *  - Elevator notifies Controller of its current location
     *
     * @param request
     * @throws ElevatorSystemException
     */
    @Override
    public void sendRequestToController(Request request) throws ElevatorSystemException {
        Building.getInstance().relayRequestToControlCenter(request);
    }

    @Override
    public void receiveControlSignal(ControlSignal signal) throws ElevatorSystemException {
        switch(signal.getSignalType()) {
            case GOTO:
                GotoSignal gotoSignal = (GotoSignal) signal;
                if(getElevatorId() == gotoSignal.getElevatorId()) {
                    moveTo(gotoSignal.getGotoFloor(), gotoSignal.getDirection());
                }
                break;
            case RIDER_ON_BOARD:
                RiderOnBoardSignal robSignal = (RiderOnBoardSignal) signal;
                //TODO: if this message is for me act on it...
                if(getElevatorId() == robSignal.getElevatorId()) {
                    enterRider();
                    System.out.println("Add " + robSignal.getDestinationFloor() + " to my floor queue...");
                    enterRider();
                    //TODO: add to queue and go...

                    moveTo(robSignal.getDestinationFloor(), getDirection());
                }
                break;
        }
    }
/**
    @Override
    public void receiveControlSignal(GotoSignal signal) throws ElevatorSystemException {
        System.out.println("I, Elevator[" + getElevatorId() + "] am ordered to go to floor " + signal.getGotoFloor());
        getNextFloorQueue().offer(signal.getGotoFloor());
        moveTo(signal.getGotoFloor(), signal.getDirection());
    }
*/
    @Override
    public void update(ControlSignal signal) throws ElevatorSystemException {
        //TODO: Elevator responds to signals of type GOTO, RIDER_ON_BOARD, ...
        if(signal.getSignalType() == ControlSignalType.GOTO || signal.getSignalType() == ControlSignalType.RIDER_ON_BOARD) {
            receiveControlSignal(signal);
        }
    }

    void enterRider() throws ElevatorSystemException {
        setNumberOfRiders(1 + getNumberOfRiders());
        System.out.println(getNumberOfRiders() + " riders in elevator " + getElevatorId());
    }
    void exitRider() throws ElevatorSystemException {
        setNumberOfRiders(getNumberOfRiders() - 1);
        //deleteObserver((Observer) rider);
    }

    int getElevatorId() {
        return elevatorId;
    }

    private boolean isDoorsClosed() {
        return doorsClosed;
    }

    private List<Observer> getRiders() {
        return riders;
    }

    private PriorityQueue<Integer> getNextFloorQueue() {
        return nextFloorQueue;
    }

    private int getNumberOfRiders() {
        return numberOfRiders;
    }

    private void setElevatorId(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    private void setSpeed() throws ElevatorSystemException {
        try {
            this.speed = Integer.parseInt(SystemConfiguration.getConfig("floor-time"));
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for floor time.");
        }
    }

    private void setLocation(int location) throws ElevatorSystemException {
        //NOTE: I used Building.getInstance().getNumberOfFloors() and got stuck for hours.
        if(location < 1 || location > Integer.parseInt(SystemConfiguration.getConfig("number-of-floors"))) {
            throw new ElevatorSystemException("Floors can only be 1 to " + Integer.parseInt(SystemConfiguration.getConfig("number-of-floors")));
        }
        this.location = location;
    }

    private void setRiders(List<Observer> riders) {
        this.riders = riders;
    }

    public void setIdle() throws ElevatorSystemException {
        try {
            System.out.println("Idling at floor " + getLocation());
            Thread.sleep(Long.parseLong(SystemConfiguration.getConfig("time-out")) * 1000L);
            int next = (getNextFloorQueue().isEmpty())? Integer.parseInt(SystemConfiguration.getConfig("default-floor")) : getNextFloorQueue().peek();
            moveTo(next, Direction.IDLE);
        } catch(InterruptedException ie) {
            throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
        }
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void setDoorsClosed(boolean doorsClosed) {
        this.doorsClosed = doorsClosed;
    }

    private void setNextFloorQueue(PriorityQueue<Integer> queue) throws ElevatorSystemException {
        if(queue == null) {
            throw new ElevatorSystemException("INTERNAL ERROR: null assigned in setNextFloorQueue()");
        }
        this.nextFloorQueue = queue;
    }

    private void setNumberOfRiders(int numberOfRiders) throws ElevatorSystemException {
        int maxCapacity = 50;
        if(numberOfRiders < 0 || numberOfRiders > maxCapacity) {
            throw new ElevatorSystemException("Elevator capacity is between 0 and " + maxCapacity);
        }
        this.numberOfRiders = numberOfRiders;
    }
}
