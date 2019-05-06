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

    private int numberOfFloors;
    private int numberOfElevators;
    private static long zeroTime;

    private List<Observer> observers = new ArrayList<>();

    private ElevatorController controlCenter = ElevatorController.getInstance();
    private static Building theBuilding = null;

    private Building() throws ElevatorSystemException {
        SystemConfiguration.initializeSystemConfiguration();
        numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        numberOfElevators = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        zeroTime = System.nanoTime();
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
            throw new ElevatorSystemException("ERROR: The observers list is null.");
        }
    }

    @Override
    public void deleteObserver(Observer o) {

    }

    @Override
    public void notifyObservers(Message message) throws ElevatorSystemException {
        for(Observer observer : getObservers()) {
            observer.update(message);
        }
    }

    @Override
    public int countObservers() throws ElevatorSystemException {

        if(getObservers() == null) {
            throw new ElevatorSystemException("ERROR: observers list is null.");
        }
        return getObservers().size();
    }

    public void relayFloorRequestToControlCenter(Message message) throws ElevatorSystemException {
        controlCenter.receiveFloorRequest(message);
    }

    public void relayElevatorRequestToControlCenter(Message message) throws ElevatorSystemException {
        controlCenter.receiveElevatorRequest(message);
    }

    public void relayLocationUpdateMessageToControlCenter(Message locationUpdateMessage) throws ElevatorSystemException {
        controlCenter.receiveLocationUpdateMessage(locationUpdateMessage);
    }

    //public void relayEnterRiderIntoElevatorMessage(Message message) throws ElevatorSystemException {
    public void relayEnterRiderIntoElevatorMessage(int origin, int destination, int elevatorId) throws ElevatorSystemException {
        //controlCenter.enterRider(message);
        controlCenter.enterRider(origin, destination, elevatorId);
    }
    public void relayExitRiderFromElevatorMessage(int elevatorId, int floorNumber) throws ElevatorSystemException {
        controlCenter.exitRider(elevatorId, floorNumber);
    }
    public void relayDeleteFloorRequestMessage(Message message, int elevatorBoardedOn) throws ElevatorSystemException {
        controlCenter.removeFloorRequest(message, elevatorBoardedOn);
    }


    private List<Observer> getObservers() {

        return observers;
    }

    public int getNumberOfFloors() {
        return this.numberOfFloors;
    }

    public int getNumberOfElevators() {
        return this.numberOfElevators;
    }


    public void generatePerson(int originFloorNumber, int destinationFloorNumber) throws ElevatorSystemException  {
        Person person = new Person(originFloorNumber, destinationFloorNumber);
        getObservers().add(person);
        person.sendMeAnElevator();
    }

    public int getNumberOfPeopleGenerated() {
        return getObservers().size();
    }

    public static long getZeroTime() {
        return zeroTime;
    }

    public void dumpRiders() throws ElevatorSystemException {
        String str = "";
        getObservers().forEach(o -> {
            Person p = (Person) o;
            //str = str.concat(p.getId() + ", " + p.getCreatedTime() + ", " + p.getStatus());
        });
    }

}
