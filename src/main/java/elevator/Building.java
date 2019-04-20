package elevator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ntessema
 *
 * Context (The strategy is Controller)
 *
 * Delegates to Controller
 */
public class Building implements Observable {

    private List<Floor> floors;
    private List<Elevator> elevators;
    private ElevatorController controlCenter;

    private List<Observer> observers; //TODO: see if this can replace the above two lists

    private static Building theBuilding = null;

    private Building (int numberOfFloors, int numberOfElevators) {

        setObservers(new ArrayList<>());
        setFloors(numberOfFloors);
        setElevators(numberOfElevators);
        setControlCenter(ElevatorController.getInstance());

        //TODO: Ready to run
    }

   public static Building getInstance() throws ElevatorSystemException {
        if(theBuilding == null) {
            synchronized(Building.class) {
                if(theBuilding == null) {
                    theBuilding = new Building(Integer.parseInt(ConfigurationManager.getConfig("number-of-floors")), Integer.parseInt(ConfigurationManager.getConfig("number-of-elevators")));
                }
            }
        }
        return theBuilding;
    }

    /**
     * ********* Observable methods ******************************
     *
     */

    @Override
    public void addObserver(Observer o) {
        getObservers().add(o);
    }

    @Override
    public void deleteObserver(Observer o) {

    }

    @Override
    public void notifyObservers(Signal signal) {
        for(Observer observer : observers) {
            observer.update(signal);
        }
        for(Floor floor : getFloors()) {
            floor.update(signal);
        }
        for(Elevator elevator : getElevators()) {
            elevator.update(signal);
        }
    }

    @Override
    public int countObservers() {
        return 0;
    }

    public void relayRequestToControlCenter(Request request) throws ElevatorSystemException {
        controlCenter.receiveRequest(request);
    }

    public void start() throws ElevatorSystemException {
        test1();
        EventLogger.getInstance().logEvent("Test 1 completed on " + new Date());
    }

    public void stop(Signal signal) throws ElevatorSystemException {
    }


    /**
     * Getters and Setters
     */

    public int getNumberOfFloors() {
        return getFloors().size();
    }

    public int getNumberOfElevators() {
        return getElevators().size();
    }

    private List<Floor> getFloors() {
        return floors;
    }

    private List<Elevator> getElevators() {
        return elevators;
    }

    private List<Observer> getObservers() {
        return observers;
    }

    private void setFloors(int numberOfFloors) {
        this.floors = new ArrayList<>();
        for(int i = 1; i <= numberOfFloors; i++) {
            Floor f = new Floor(((i == numberOfFloors) ? false : true), ((i == 1) ? false : true));
            this.floors.add(f);
            addObserver(f);
        }
    }

    private void setElevators(int numberOfElevators) {
        this.elevators = new ArrayList<>();
        for(int i = 1; i <= numberOfElevators; i++) {
            Elevator e = new Elevator(Double.parseDouble(ConfigurationManager.getConfig("speed")), Integer.parseInt(ConfigurationManager.getConfig("default-floor")));
            this.elevators.add(e);
            addObserver(e);
        }
    }

    private void setObservers(List<Observer> observers) {
        this.observers = observers;

    }

    private void setControlCenter(ElevatorController controlCenter) {
        this.controlCenter = controlCenter;
    }

    /**
     * Utility methods
     */

    private void test1() throws ElevatorSystemException {

        int elevatorId = getNumberOfElevators();
        //do {
        for (int i = 2; i <= getNumberOfFloors(); i++) {
            Payload payload = new Payload(
                    PayloadType.CONTROLLER_TO_ELEVATOR__GOTO_FLOOR_DIRECTION,
                    elevatorId, i, Direction.IDLE, true);
            notifyObservers(new Signal(ElementType.CONTROLLER, ElementType.ELEVATOR, elevatorId, payload));//TODO: delete this. for demo only
        }
        try {
            Thread.sleep(2000L);
        } catch(Exception e) {

        }
        for (int i = getNumberOfFloors() - 1; i >= 1; i--) {
            getElevators().get(elevatorId-1).move(i);//TODO: delete this. for demo only
        }
        //} while(true);
    }

}
