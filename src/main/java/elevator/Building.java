package elevator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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

    public void relayFloorRequestToControlCenter(int fromFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        controlCenter.receiveFloorRequest(fromFloorNumber, desiredDirection);
    }

    public void relayElevatorRequestToControlCenter(int elevatorId, int destinationFloor, int originFloor, int personId) throws ElevatorSystemException {
        controlCenter.receiveElevatorRequest(elevatorId, destinationFloor, originFloor, personId);
    }

    public void relayLocationUpdateMessageToControlCenter(int elevatorId, int location, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        controlCenter.receiveLocationUpdateMessage(elevatorId, location, direction, directionDispatchedFor);
    }

    public void relayExitRiderFromElevatorMessage(int elevatorId, int floorNumber, int personId) throws ElevatorSystemException {
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
        //TODO: Person generation code;
        try {
            Random random = new Random(97);
            int numberOfFloors = getNumberOfFloors();
            long elapsedSeconds = TimeUnit.MILLISECONDS.convert((System.nanoTime() - Building.getInstance().getZeroTime()), TimeUnit.NANOSECONDS);
            while (elapsedSeconds < 120000L) {
                int origin = 1 + random.nextInt(numberOfFloors);
                int destination = 1 + random.nextInt(numberOfFloors);
                if(origin == destination) {
                    System.out.println("ORIGIN = DESTINATION");
                }
                generatePerson(origin, destination);
                Thread.sleep(2000L);
                elapsedSeconds = TimeUnit.MILLISECONDS.convert((System.nanoTime() - Building.getInstance().getZeroTime()), TimeUnit.NANOSECONDS);
            }
        } catch(ElevatorSystemException ese) {
            ese.getMessage();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Person\tStart Floor\tEnd Floor\tDirection\tWait Time\tRide Time\tTotal Time\n");
        sb.append("------\t-----------\t---------\t---------\t---------\t---------\t----------\n");
        getObservers().forEach(rider -> {
            Person p = (Person) rider;
            String id = Integer.toString(p.getId());
            String origin = Integer.toString(p.getOriginFloor());
            String destination = Integer.toString(p.getDestinationFloor());
            String direction = Utility.evaluateDirection(p.getOriginFloor(), p.getDestinationFloor()).toString();
            String waitTime = Utility.nanoToRoundedSeconds(p.getBoardingTime() - p.getCreatedTime(), 1);
            String rideTime = Utility.nanoToRoundedSeconds(p.getExitTime() - p.getBoardingTime(), 1);
            String totalTime = Utility.nanoToRoundedSeconds(p.getExitTime() - p.getCreatedTime(), 1);
            sb.append(
                    Utility.formatColumnString(id, 6) + "\t" +
                    Utility.formatColumnString(origin, 11) + "\t" +
                    Utility.formatColumnString(destination, 9) + "\t" +
                    Utility.formatColumnString(direction, 9) + "\t" +
                    Utility.formatColumnString(waitTime, 9) + "\t" +
                    Utility.formatColumnString(rideTime, 9) + "\t" +
                    Utility.formatColumnString(totalTime, 10) + "\n");
        });
        return sb.toString();
    }


    public static long getZeroTime() {
        return zeroTime;
    }
    public static Instant getZeroInstant() { return zeroInstant; }

}
