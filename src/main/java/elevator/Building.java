package elevator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ntessema
 *
 * Context (The strategy is Controller)
 *
 * Delegates to Controller
 */
public class Building implements Observable {

    private List<Floor> floors = new ArrayList<>(); //TODO: no longer needed...
    private List<Elevator> elevators = new ArrayList<>();
    private List<Rider> riders = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>(); //TODO: see if this can replace the above two lists

    private ElevatorController controlCenter = ElevatorController.getInstance();
    private static Building theBuilding = null;

    private Building() throws ElevatorSystemException {
        setFloors();
        setElevators();
    }

   public static Building getInstance() throws ElevatorSystemException {
        if(theBuilding == null) {
           synchronized(Building.class) {
                if(theBuilding == null) {
                    theBuilding = new Building();
                }
            }
        }
        return theBuilding;
    }

    /**
     * Observable methods
     */

    @Override
    public void addObserver(Observer o) throws ElevatorSystemException {
        try {
            getObservers().add(o);
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("INTERNAL ERROR: Observers list is null.");
        }
    }

    @Override
    public void deleteObserver(Observer o) {

    }

    @Override
    public void notifyObservers(ControlSignal signal) throws ElevatorSystemException {
        for(Observer observer : getObservers()) {
            System.out.println("notifying all observers in building with GotoSignal....");
            observer.update(signal);
        }
    }

    @Override
    public int countObservers() throws ElevatorSystemException {

        if(getObservers() == null) {
            throw new ElevatorSystemException("INTERNAL ERROR: observers list of Building is null.");
        }
        return getObservers().size();
    }

    public void relayRequestToControlCenter(Request request) throws ElevatorSystemException {
        controlCenter.receiveRequest(request);
    }

    public void relayNotificationToControlCenter(Notification notification) throws ElevatorSystemException {
        controlCenter.receiveNotification(notification);
    }


    /**
     * Getters and Setters
     */

    private List<Floor> getFloors() {
        return floors;
    }

    private List<Elevator> getElevators() {
        return elevators;
    }

    private List<Rider> getRiders() {
        return riders;
    }

    private List<Observer> getObservers() {

        return observers;
    }

    private void setFloors() throws ElevatorSystemException {
        try {
            int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfig("number-of-floors"));
            this.floors = new ArrayList<>();
            for (int i = 1; i <= numberOfFloors; i++) {
                Floor f = new Floor(((i == numberOfFloors) ? false : true), ((i == 1) ? false : true));
                addFloor(f);
            }
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for number of floors.");
        }
    }

    private void setElevators() throws ElevatorSystemException {
        try {
            this.elevators = new ArrayList<>();
            int numberOfElevators = Integer.parseInt(SystemConfiguration.getConfig("number-of-elevators"));
            for (int i = 1; i <= numberOfElevators; i++) {
               Elevator e = new Elevator();
               addElevator(e);
           }
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for number of elevators.");
        }
    }

    public int getNumberOfFloors() {
        return getFloors().size();
    }

    public int getNumberOfElevators() {
        return Integer.parseInt(SystemConfiguration.getConfig("number-of-elevators"));
    }

    private void addFloor(Floor floor) throws ElevatorSystemException {
        try {
            getFloors().add(floor); //TODO: floors are no longer needed... new design.
            //getObservers().add(floor);
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("INTERNAL ERROR: floors/observers list is null...");
        }
    }

    private void addElevator(Elevator elevator) throws ElevatorSystemException {
        try {
            getElevators().add(elevator);
            getObservers().add(elevator);
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("INTERNAL ERROR: elevators/observers list is null");
        }
    }

    public void generatePerson(int originFloorNumber, int destinationFloorNumber) throws ElevatorSystemException  {
        Person person = new Person(originFloorNumber, destinationFloorNumber);
        getRiders().add(person);
        getObservers().add(person);
        person.requestElevator();
    }

}
