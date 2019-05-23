package elevator;

import java.time.Instant;
import java.util.Arrays;
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

    public static int TEST;//TODO: absolutely for the tests only!!!

    private int numberOfFloors;
    private int numberOfElevators;
    private static long zeroTime;
    private static Instant zeroInstant;

    private List<Observer> observers = new CopyOnWriteArrayList<>();
    /** Chosen over ArrayList to avoid a recurring ConcurrentModificationException while updating in iterator.*/

    private ElevatorController controlCenter = ElevatorController.getInstance();
    private static Building theBuilding = null;

    private Building() throws ElevatorSystemException {
        SystemConfiguration.initializeSystemConfiguration();
        numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        numberOfElevators = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
        zeroTime = System.nanoTime();
        zeroInstant = Instant.now();
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
            getObservers().add(o);
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

    public int getNumberOfFloors() {
        return this.numberOfFloors;
    }

    public int getNumberOfElevators() {
        return this.numberOfElevators;
    }


    public void generatePerson(int originFloorNumber, int destinationFloorNumber) throws ElevatorSystemException  {
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
            long elapsedSeconds = TimeUnit.MILLISECONDS.convert((System.nanoTime() - Building.getInstance().getZeroTime()), TimeUnit.NANOSECONDS);
            final long SIMULATION_DURATION = Long.parseLong(SystemConfiguration.getConfiguration("simulationDuration"));
            final long CREATION_RATE = Long.parseLong(SystemConfiguration.getConfiguration("creationRate"));
            while (elapsedSeconds < SIMULATION_DURATION * 1000L) {
                int origin = 1 + random.nextInt(numberOfFloors);
                int destination = 1 + random.nextInt(numberOfFloors);
                if(origin == destination) {
                    continue;
                }
                generatePerson(origin, destination);
                Thread.sleep(CREATION_RATE * 1000L);
                elapsedSeconds = TimeUnit.MILLISECONDS.convert((System.nanoTime() - Building.getInstance().getZeroTime()), TimeUnit.NANOSECONDS);
            }
        } catch(ElevatorSystemException ese) {
            ese.getMessage();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    public String generateReport() {
        List<Person> list = getObservers().stream().map(observer -> (Person) observer).collect(Collectors.toList());
        return Utility.generateReport(list);
    }

    static long getZeroTime() {
        return zeroTime;
    }
    static Instant getZeroInstant() { return zeroInstant; }

}
