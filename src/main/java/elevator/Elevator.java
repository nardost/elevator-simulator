package elevator;

import gui.ElevatorDisplay;

import java.util.*;

import static gui.ElevatorDisplay.Direction.DOWN;
import static gui.ElevatorDisplay.Direction.UP;

enum Direction {IDLE, UP, DOWN};

class Elevator implements GenericElevator {

    private int elevatorId;
    private int speed;
    private int location;
    private Direction direction;
    private boolean doorsClosed;

    private int numberOfRiders;

    private PriorityQueue<Integer> nextFloorQueue = new PriorityQueue<>(Comparator.naturalOrder());

    private static int instanceCounter = 0;

    Elevator() throws ElevatorSystemException {

        setElevatorId(++instanceCounter);
        setSpeed();
        setDirection(Direction.IDLE);
        setDoorsClosed(true);
        try {
            setLocation(Integer.parseInt(SystemConfiguration.getConfig("default-floor")));
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for default floor.");
        }
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

    public void move(Direction direction) throws ElevatorSystemException {

        int floor = getNextStop();
        setDirection(direction);

        if(!areDoorsClosed()) {
            closeDoors();
        }

        long floorTime = 1000L * (long) getSpeed();

        if(getLocation() < floor) {
            for (int i = getLocation(); i <= floor; i++) {
                //TODO: Before moving to the next floor, check if there is a new stop by...
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), UP);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
                }
                setLocation(i);
                Message locationUpdateMessage = new LocationUpdateMessage(getElevatorId(), getLocation(), getDirection());
                Building.getInstance().relayLocationUpdateMessageToControlCenter(locationUpdateMessage);//new Notification(getElevatorId(), i, getDirection()));
            }
        } else {
            for (int i = getLocation(); i >= floor; i--) {
                //TODO: Before moving to the next floor, check if there is a new stop by...
                ElevatorDisplay.getInstance().updateElevator(getElevatorId(), i, getNumberOfRiders(), DOWN);
                try {
                    Thread.sleep(floorTime);
                } catch(InterruptedException ie) {
                    throw new ElevatorSystemException("INTERNAL ERROR: Thread interrupted.");
                }
                setLocation(i);
                Message locationUpdateMessage = new LocationUpdateMessage(getElevatorId(), getLocation(), getDirection());
                Building.getInstance().relayLocationUpdateMessageToControlCenter(locationUpdateMessage);
            }
        }
    }

    @Override
    public void openDoors() throws ElevatorSystemException {
        setDoorsClosed(false);
        try {
            long doorTime = Long.parseLong(SystemConfiguration.getConfig("door-time"));
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
    }

    void enterRider() throws ElevatorSystemException {
        setNumberOfRiders(1 + getNumberOfRiders());
        Building.print(getNumberOfRiders() + " riders in elevator " + getElevatorId());
    }
    void exitRider() throws ElevatorSystemException {
        setNumberOfRiders(getNumberOfRiders() - 1);
    }

    int getElevatorId() {
        return elevatorId;
    }

    PriorityQueue<Integer> getNextFloorQueue() {
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

    void setLocation(int location) throws ElevatorSystemException {
        //NOTE: I used Building.getInstance().getNumberOfFloors() and got stuck for hours.
        if(location < 1 || location > Integer.parseInt(SystemConfiguration.getConfig("number-of-floors"))) {
            throw new ElevatorSystemException("Floors can only be 1 to " + Integer.parseInt(SystemConfiguration.getConfig("number-of-floors")));
        }
        this.location = location;
    }

    void setIdle() throws ElevatorSystemException {

        Building.print("Idling at floor " + getLocation());

        try {
            Thread.sleep(Long.parseLong(SystemConfiguration.getConfig("time-out")) * 1000L);
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

    private void setNumberOfRiders(int numberOfRiders) throws ElevatorSystemException {
        int maxCapacity = 50;
        if(numberOfRiders < 0 || numberOfRiders > maxCapacity) {
            throw new ElevatorSystemException("Elevator capacity is between 0 and " + maxCapacity);
        }
        this.numberOfRiders = numberOfRiders;
    }
    void addNextStop(int next) {

        getNextFloorQueue().offer(next);
        Building.print("Next stops for Elevator[" + getElevatorId() + "] is: " + getNextFloorQueue().peek());
    }

    int getNextStop() {
        return getNextFloorQueue().poll();
    }
}
