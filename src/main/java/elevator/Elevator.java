package elevator;

import gui.ElevatorDisplay;

import java.util.*;

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

    private int numberOfRiders;

    private PriorityQueue<Integer> nextFloorQueueNatural = new PriorityQueue<>(Comparator.naturalOrder());
    private PriorityQueue<Integer> nextFloorQueueReversed = new PriorityQueue<>(Comparator.reverseOrder());

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

    public void move() throws ElevatorSystemException {

        long floorTime = 1000L * (long) getSpeed();

        if(iHaveNoMoreStops()) {
            closeDoors();
            if(getLocation() != Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor"))) {
                setIdle();
            }
            EventLogger.print("E-" + getElevatorId() + " has accomplished its mission.");
            return;
        }

        int floor = peekNextStop();

        if(!areDoorsClosed()) {
            closeDoors();
        }

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
                Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());

                if(Building.TEST == 2 && i == 5 && Building.getInstance().getNumberOfPeopleGenerated() == 1) {
                    Building.getInstance().generatePerson(15, 19);
                    i = getLocation();
                }
                if(Building.TEST == 4) {
                    if(i == 5 && Building.getInstance().getNumberOfPeopleGenerated() == 1) {
                        Building.getInstance().generatePerson(8, 17);
                        i = getLocation();
                    } else if(i == 6 && Building.getInstance().getNumberOfPeopleGenerated() == 2) {
                        Building.getInstance().generatePerson(1, 9);
                        i = getLocation();
                    }
                }
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
                Building.getInstance().relayLocationUpdateMessageToControlCenter(getElevatorId(), getLocation(), getDirection(), getDispatchedToServeDirection());
                if(Building.TEST == 3 && i == 15 && Building.getInstance().getNumberOfPeopleGenerated() == 1) {
                    Building.getInstance().generatePerson(10, 1);
                    i = getLocation();
                }
            }
        }
        openDoors();
        closeDoors();
    }

    @Override
    public void openDoors() throws ElevatorSystemException {
        setDoorsClosed(false);
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

    @Override
    public void closeDoors() {
        setDoorsClosed(true);
        ElevatorDisplay.getInstance().closeDoors(getElevatorId());
    }

    void enterRider() throws ElevatorSystemException {
        setNumberOfRiders(1 + getNumberOfRiders());
        EventLogger.print("E-" + getElevatorId() + " has " + getNumberOfRiders() + " riders.");
    }
    void exitRider() throws ElevatorSystemException {
        setNumberOfRiders(getNumberOfRiders() - 1);
    }

    int getElevatorId() {
        return elevatorId;
    }

    PriorityQueue<Integer> getNextFloorQueue() {
        if(getDirection() == Direction.UP) {
            return nextFloorQueueNatural;
        }
        return nextFloorQueueReversed;
    }

    private int getNumberOfRiders() {
        return numberOfRiders;
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

        EventLogger.print("E-" + getElevatorId() + " idling at F-" + getLocation());

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

    private void setNumberOfRiders(int numberOfRiders) throws ElevatorSystemException {
        int maxCapacity = Integer.parseInt(SystemConfiguration.getConfiguration("elevatorCapacity"));
        if(numberOfRiders < 0 || numberOfRiders > maxCapacity) {
            throw new ElevatorSystemException("Elevator capacity is between 0 and " + maxCapacity);
        }
        this.numberOfRiders = numberOfRiders;
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

    void addNextStop(int next) throws ElevatorSystemException {
        if(!getNextFloorQueue().contains(next)) {
            getNextFloorQueue().offer(next);
            EventLogger.print("E-" + getElevatorId() + " next stops: " + listNextStops());
        } else {
            EventLogger.print("E-" + getElevatorId() + " already contains next stop F-" + next + ". Next stops: " + listNextStops());
        }
    }

    Integer peekNextStop() {
        if(getNextFloorQueue().peek() != null) {
            return getNextFloorQueue().peek();
        }
        return null;
    }
    Integer pollNextStop() throws ElevatorSystemException {
        if(peekNextStop() != null) {
            int next = getNextFloorQueue().poll();
            EventLogger.print("E-" + getElevatorId() + " next stops: " + listNextStops());
            return next;
        }
        return null;
    }
    boolean iHaveNoMoreStops() {
        if(peekNextStop() == null) {
            return true;
        }
        return false;
    }
    String listNextStops() {
        Iterator iterator = getNextFloorQueue().iterator();
        StringBuilder sb = new StringBuilder("[");
        while(iterator.hasNext()) {
            sb.append(iterator.next());
            sb.append(", ");
        }
        int l = sb.length();
        if(l > 2) {
            sb.deleteCharAt(l - 1);
            sb.deleteCharAt(l - 2);
        }
        sb.append("]");
        return sb.toString();
    }

}
