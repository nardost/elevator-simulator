package elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ntessema
 *
 * Context (The strategy is Controller)
 *
 * Delegates to Controller
 */
public class Building implements Controller, Observable {

    private Controller controller;
    private EventLogger logger;

    private List<Floor> floors;
    private List<Elevator> elevators;
    private List<Observer> observers; //TODO: see if this can replace the above two lists
    private boolean isActive;

    private static Building theBuilding = null;

    /**
     * ****** Configuration data *****************************************
     */

    //TODO: this should be changed... read config from JSON ...
    private double DEFAULT_SPEED = 1.0;
    private int INITIAL_LOCATION = 1;
    private Motion ELEVATOR_STATUS = Motion.IDLE;
    private Doors DOOR_STATUS = Doors.CLOSED;

    /**
     * *******************************************************************
     */

    private Building (int numberOfFloors, int numberOfElevators, Controller controller, EventLogger logger) {

        this.logger = logger;
        this.controller = controller;
        this.isActive = false;
        this.observers = new ArrayList<>();
        createFloors(numberOfFloors);
        createElevators(numberOfElevators);

        this.observers = Stream.concat(elevators.stream(), floors.stream()).collect(Collectors.toList());

        //TODO: Ready to run
    }

    //Singleton
    public static Building setupBuilding(int numberOfFloors, int numberOfElevators, String controllerIdentifier, String typeOfLogger) throws ElevatorSystemException {
        if(theBuilding == null) {
            synchronized(Building.class) {
                if(theBuilding == null) {
                    //TODO: Controller should be dynamically set
                    //TODO: read configuration JSON...
                    theBuilding = new Building(numberOfFloors, numberOfElevators, new ElevatorController(), EventLogger.getInstance(typeOfLogger));
                }
            }
        }
        return theBuilding;
    }


    @Override
    public void signalAll(Signal signal) {
        controller.signalAll(signal);
        //TODO: cut paste to controller (delegation)
        for(Floor floor : floors) {
            floor.receiveSignal(signal);
        }
        for(Elevator elevator : elevators) {
            elevator.receiveSignal(signal);
        }
    }

    @Override
    public void receiveNotification(Signal signal) {
        controller.receiveNotification(signal);
    }

    @Override
    public void run(Signal signal) throws ElevatorSystemException {
        controller.run(signal);
        do {
            for (int i = 2; i <= floors.size(); i++) {
                signalAll(new Signal(Receiver.ELEVATOR, 2, i));//TODO: delete this. for demo only
            }
            try {
                Thread.sleep(2000L);
            } catch(Exception e) {

            }
            for (int i = floors.size() - 1; i >= 1; i--) {
                elevators.get(2).move(i);//TODO: delete this. for demo only
            }
        } while(true);
    }

    @Override
    public void stop(Signal signal) throws ElevatorSystemException {
        controller.stop(signal);
    }


    public int getNumberOfFloors() {
        return this.floors.size();
    }

    public int getNumberOfElevators() {
        return this.elevators.size();
    }

    private void createFloors(int numberOfFloors) {
        this.floors = new ArrayList<>();
        for(int i = 1; i <= numberOfFloors; i++) {
            Floor f = new Floor(i, ((i == numberOfFloors) ? false : true), ((i == 1) ? false : true), this, logger);
            floors.add(f);
            addObserver(f);
        }
    }

    private void createElevators(int numberOfElevators) {
        this.elevators = new ArrayList<>();
        for(int i = 1; i <= numberOfElevators; i++) {
            Elevator e = new Elevator(i, DEFAULT_SPEED, INITIAL_LOCATION, ELEVATOR_STATUS, DOOR_STATUS, this);
            elevators.add(e);
            addObserver(e);
        }
    }

    public void start() throws ElevatorSystemException {
        run(new Signal(Receiver.ELEVATOR, 1, 1)); //TODO: define some startup signal
    }


    /**
     * ********* Observable methods ******************************
     *
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
}
