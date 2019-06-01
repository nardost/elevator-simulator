package elevator;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private List<Observer> observers = new CopyOnWriteArrayList<>();
    /** Chosen over ArrayList to avoid a recurring ConcurrentModificationException while updating in iterator.*/

    private ElevatorController controlCenter = ElevatorController.getInstance();
    private static Building theBuilding = null;

    private Building() throws ElevatorSystemException {

        numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        numberOfElevators = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        zeroTime = System.currentTimeMillis();
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

    public void start() throws ElevatorSystemException {
        controlCenter.run();
    }

    /**
     * Observable methods
     */

    @Override
    public void addObserver(Observer o) throws ElevatorSystemException {
        try {
            Validator.validateNotNull(o);
            getObservers().add(o);
        } catch(ElevatorSystemException ese) {
            throw new ElevatorSystemException("Null not allowed in observers list.");
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("ERROR: The observers list is null.");
        }
    }

    @Override
    public void notifyObservers(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        /**
         * If ArrayList is used, the following lines will throw a ConcurrentModificationException (Test3).
         * CopyOnWriteArrayList was used and the problem disappeared. The Javadoc says the following.
         * This is ordinarily too costly, but may be more efficient than alternatives when traversal operations
         * vastly outnumber mutations, and is useful when you cannot or don't want to synchronize traversals,
         * yet need to preclude interference among concurrent threads.
         * */
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateFloorNumber(elevatorLocation);
        Iterator iterator = getObservers().iterator();
        while (iterator.hasNext()) {
            Observer rider = (Observer) iterator.next();
            rider.update(elevatorId, elevatorLocation, direction, directionDispatchedFor);
        }
    }

    @Override
    public int countObservers() throws ElevatorSystemException {
        if(getObservers() == null) {
            throw new ElevatorSystemException("ERROR: observers list is null.");
        }
        return getObservers().size();
    }

    void relayFloorRequestToControlCenter(int fromFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        controlCenter.receiveFloorRequest(fromFloorNumber, desiredDirection);
    }

    void relayElevatorRequestToControlCenter(int elevatorId, int destinationFloor, int originFloor, int personId) throws ElevatorSystemException {
        controlCenter.receiveElevatorRequest(elevatorId, destinationFloor, originFloor, personId);
    }

    void relayLocationUpdateMessageToControlCenter(int elevatorId, int location, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        controlCenter.receiveLocationUpdateMessage(elevatorId, location, direction, directionDispatchedFor);
    }

    void relayExitRiderFromElevatorMessage(int elevatorId, int floorNumber, int personId) throws ElevatorSystemException {
        controlCenter.exitRider(elevatorId, floorNumber, personId);
    }

    private List<Observer> getObservers() {

        return observers;
    }

    int getNumberOfFloors() {
        return this.numberOfFloors;
    }

    int getNumberOfElevators() {
        return this.numberOfElevators;
    }


    void generatePerson(int originFloorNumber, int destinationFloorNumber) throws ElevatorSystemException  {
        Validator.validateFloorNumber(originFloorNumber);
        Validator.validateFloorNumber(destinationFloorNumber);
        Person person = new Person(originFloorNumber, destinationFloorNumber);
        Direction desiredDirection = (originFloorNumber < destinationFloorNumber) ? Direction.UP : Direction.DOWN;
        addObserver(person);
        EventLogger.print("Person P" + person.getId() + " created on Floor " + originFloorNumber + ", wants to go " + desiredDirection.toString() + " to Floor " + destinationFloorNumber);
        person.sendMeAnElevator();
    }

    public void run() {
        try {
            Random random = new Random(97);
            int numberOfFloors = getNumberOfFloors();
            long elapsedSeconds = TimeUnit.SECONDS.convert((System.currentTimeMillis() - Building.getInstance().getZeroTime()), TimeUnit.MILLISECONDS);
            final long SIMULATION_DURATION = Long.parseLong(SystemConfiguration.getConfiguration("simulationDuration"));
            final long CREATION_RATE = Long.parseLong(SystemConfiguration.getConfiguration("creationRate"));
            while (elapsedSeconds < SIMULATION_DURATION) {
                int origin = 1 + random.nextInt(numberOfFloors);
                int destination = 1 + random.nextInt(numberOfFloors);
                while(origin == destination) {
                    destination = 1 + random.nextInt(numberOfFloors);
                }
                generatePerson(origin, destination);
                Thread.sleep(CREATION_RATE * 1000L);
                elapsedSeconds = TimeUnit.SECONDS.convert((System.currentTimeMillis() - Building.getInstance().getZeroTime()), TimeUnit.MILLISECONDS);
            }
            EventLogger.print("Done with rider generation.");
        } catch(ElevatorSystemException ese) {
            ese.getMessage();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    public String generateReport() throws ElevatorSystemException {
        List<Person> list = getObservers().stream().map(observer -> (Person) observer).collect(Collectors.toList());
        return Utility.generateReport(list);
    }

    static long getZeroTime() {
        return zeroTime;
    }

}
