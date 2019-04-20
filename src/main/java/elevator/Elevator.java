package elevator;

import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

enum Direction {IDLE, UP, DOWN}

class Elevator implements GenericElevator, Controllable, Observer, Observable {

    private int elevatorId;
    private double speed;
    private int location;
    private Direction direction;
    private boolean doorsClosed;
    private List<Rider> riders;
    private List<Observer> observers;

    private PriorityQueue<Integer> nextFloor;

    private static int instanceCounter = 0;

    public Elevator(double speed, int initialLocation) {

        setElevatorId(++instanceCounter);
        setSpeed(speed);
        setLocation(initialLocation);
        setDirection(Direction.IDLE);
        setDoorsClosed(true);

        setNextFloor(new PriorityQueue<>());//TODO: Collections.reverseOrder() dynamically on UP | DOWN
    }

   public void run() throws ElevatorSystemException {
        do {
            //TODO: get next destination from queue and move
            move(nextFloor.poll());
        } while(true);
    }

    @Override
    public double getSpeed() {
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
    public void move(int floor) throws ElevatorSystemException {
        if(getDirection() == Direction.IDLE) {
            if(floor == getLocation()) {
                return;
            }
            setDirection((floor > getLocation()) ? Direction.UP : Direction.DOWN);
            long delay = ((long) Math.abs(floor - getLocation()) / (long) getSpeed());
            System.out.println(new Date() + " Elevator " + getElevatorId() + " moving to floor " + floor);
            EventLogger.getInstance().logEvent(new Date() + " Elevator " + getElevatorId() + " moving to floor " + floor);
            try {
                Thread.sleep(delay * 1000L);
            } catch(InterruptedException ie) {
                throw new ElevatorSystemException("Thread interrupted.");
            }
            System.out.println(new Date() + " Elevator " + getElevatorId() + " at floor " + floor);
            EventLogger.getInstance().logEvent(new Date() + " Elevator " + getElevatorId() + " at floor " + floor);
            setLocation(floor);
            setDirection(Direction.IDLE);
            setDoorsClosed(false);
            Request request = new Request(floor, Direction.UP);
            this.sendRequestToController(request);

            //TODO: delay while door is open
            try {
                Thread.sleep(1000L);
            } catch(InterruptedException ie) {
                throw new ElevatorSystemException("Thread interrupted.");
            }
        }
        //TODO: move...
    }

    @Override
    public void openDoors() {
        setDoorsClosed(false);
    }

    @Override
    public void closeDoors() {
        setDoorsClosed(true);
    }

    @Override
    public void sendRequestToController(Request request) throws ElevatorSystemException {
        Building.getInstance().relayRequestToControlCenter(request);
    }

    @Override
    public void receiveControlSignal(Signal signal) {
        getNextFloor().offer(signal.getFloorNumberFromPayload());
    }

    @Override
    public void update(Signal signal) { //TODO: Building updates elevator with signal. Floors acts on signal
        if(signal.getReceiver() == ElementType.ELEVATOR && signal.getReceiverId() == getElevatorId()) {
            receiveControlSignal(signal);
        }
    }

    /**
     * ********* Observable methods *****************
     */

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void deleteObserver(Observer o) {

    }

    @Override
    public void notifyObservers(Signal signal) {
        for(Observer observer : observers) {
            observer.update(signal);
        }
    }

    @Override
    public int countObservers() {
        return 0;
    }

    public void enterRider(Rider rider) {
        riders.add(rider);
        addObserver((Observer) rider);
    }
    public void exitRider(Rider rider) {
        riders.remove(rider);
        deleteObserver((Observer) rider);
    }

    public int getElevatorId() {
        return elevatorId;
    }

    private PriorityQueue<Integer> getNextFloor() {
        return nextFloor;
    }

    private void setElevatorId(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    private void setSpeed(double speed) {
        this.speed = speed;
    }

    private void setLocation(int location) {
        this.location = location;
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void setDoorsClosed(boolean doorsClosed) {
        this.doorsClosed = doorsClosed;
    }

    private void setRiders(List<Rider> riders) {
        this.riders = riders;
    }

    private void setObservers(List<Observer> observers) {
        this.observers = observers;
    }

    private void setNextFloor(PriorityQueue<Integer> nextFloor) {
        this.nextFloor = nextFloor;
    }
}
