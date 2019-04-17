package elevator;

import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

enum Motion {IDLE, MOVING_UP, MOVING_DOWN}
enum Doors {OPEN, CLOSED}

public class Elevator implements ElevatorBehavior, Controllable, Observer, Observable {

    private int id;
    private double speed;
    private boolean isActive;
    private int location;
    private Motion motion;
    private Doors doors;
    private List<Rider> riders;
    private List<Observer> observers;
    private Controller controller;

    private PriorityQueue<Integer> nextFloor;

    public Elevator(int id, double speed, int initialLocation, Motion idleOrUpOrDown, Doors openOrClosed, Controller theBuilding) {
        this.id = id;
        this.speed = speed;
        this.isActive = false; //TODO: check if this is really necessary
        this.location = initialLocation;
        this.motion = idleOrUpOrDown;
        this.doors = openOrClosed;
        this.controller = theBuilding;

        this.nextFloor = new PriorityQueue<>();//TODO: Collections.reverseOrder() dynamically on UP | DOWN
    }

    @Override
    public void run() throws ElevatorSystemException {
        do {
            this.isActive = true;
            //TODO: get next destination from queue and move
            move(nextFloor.poll());
        } while(true);
    }

    @Override
    public void stop() {
        this.isActive = false;
        //TODO
    }

    @Override
    public double getSpeed() {
        return this.speed;
    }

    @Override
    public Motion getMotion() {
        return this.motion;
    }

    @Override
    public Doors getDoorsStatus() {
        return this.doors;
    }

    @Override
    public int getLocation() {
        return this.location;
    }

    @Override
    public void move(int floor) throws ElevatorSystemException {
        if(this.motion == Motion.IDLE) {
            if(floor == this.location) {
                return;
            }
            this.motion = (floor > this.location) ? Motion.MOVING_UP : Motion.MOVING_DOWN;
            long delay = ((long) Math.abs(floor - this.location) / (long) this.speed);
            System.out.println(new Date() + " Elevator " + this.id + " moving to floor " + floor);
            try {
                Thread.sleep(delay * 1000L);
            } catch(InterruptedException ie) {
                throw new ElevatorSystemException("Thread interrupted.");
            }
            System.out.println(new Date() + " Elevator " + this.id + " at floor " + floor);
            this.location = floor;
            this.motion = Motion.IDLE;
            this.doors = Doors.OPEN;
            Signal signal = new Signal(Receiver.BUILDING, 0, floor);
            this.notifyController(signal);

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
        doors = Doors.OPEN;
    }

    @Override
    public void closeDoors() {
        doors = Doors.CLOSED;
    }

    @Override
    public void notifyController(Signal signal) {
        controller.receiveNotification(signal);
    }

    @Override
    public void receiveSignal(Signal signal) {
        if(signal.getReceiverType() == Receiver.ELEVATOR && signal.getReceiverId() == this.id) {
            //TODO: act on received signal
            this.nextFloor.offer(signal.getGotoFloor());
        }
    }

    @Override
    public void update(Signal signal) {

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
    public void deleteObservers() {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public void clearChanged() {

    }

    @Override
    public boolean hasChanged() {
        return false;
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

    public int getId() {
        return id;
    }
}
