package elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ntessema
 *
 * Context (The strategy is Controller)
 *
 * Delegates to Controller
 */
public class Building implements Observable {

    private int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfig("number-of-floors"));
    private int numberOfElevators = Integer.parseInt(SystemConfiguration.getConfig("number-of-elevators"));
    private static long zeroTime;

    private List<Observer> observers = new ArrayList<>();

    private ElevatorController controlCenter = ElevatorController.getInstance();
    private static Building theBuilding = null;

    private Building() throws ElevatorSystemException {
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
            throw new ElevatorSystemException("INTERNAL ERROR: Observers list is null.");
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
            throw new ElevatorSystemException("INTERNAL ERROR: observers list of Building is null.");
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

    public void relayEnterRiderIntoElevatorMessage(Message message) throws ElevatorSystemException {
        controlCenter.enterRider(message);
    }
    public void relayExitRiderFromElevatorMessage(int elevatorId) throws ElevatorSystemException {
        controlCenter.exitRider(elevatorId);
    }
    public void relayDeleteFloorRequestMessage(Message message) {
        FloorRequest floorRequest = (FloorRequest) message;
        controlCenter.removeFloorRequest(floorRequest.getFromFloorNumber(), floorRequest.getDesiredDirection());
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

    public static long getZeroTime() {
        return zeroTime;
    }

    public static String formatElapsedTime(long nanoTime) {
        long elapsedTime = nanoTime - getZeroTime();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static void print(String msg) {
        System.out.println(formatElapsedTime(System.nanoTime()) + " " + msg);
    }

}
