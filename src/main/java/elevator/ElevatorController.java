package elevator;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author ntessema
 *
 * Context. The Strategy is Controller
 *
 */

class ElevatorController {

    private Controller controller;

    private List<Elevator> elevators = new ArrayList<>();
    private Hashtable<Integer, Direction> floorRequests = new Hashtable<>();

    private static ElevatorController controlCenter = null;

    private ElevatorController() throws ElevatorSystemException {
        SystemConfiguration.initializeSystemConfiguration();//TODO: Important!!!
        setController(ControllerFactory.createController());
        setElevators();
    }

    static ElevatorController getInstance() throws ElevatorSystemException {
        if(controlCenter == null) {
            synchronized(ElevatorController.class) {
                if(controlCenter == null) {
                    controlCenter = new ElevatorController();
                }
            }
        }
        return controlCenter;
    }

    void commandElevator(int elevatorId, int floorNumber, Direction direction) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        e.addNextStop(floorNumber);
        EventLogger.print("Next Stop added to queue " + floorNumber);
        e.move(direction);
    }

    void stopElevatorAndLetRiderIn(int elevatorId, int floorNumber) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        //TODO: poll() queue -> oldDestination, moveTo(floorNumber)
        e.openDoors();
        e.setLocation(floorNumber);
        //e.move(e.getDirection());
    }

    void setIdleAndReturnToDefaultFloor(int elevatorId) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        //e.closeDoors();
        e.setIdle();
        e.addNextStop(Integer.parseInt(SystemConfiguration.getConfiguration("defaultFloor")));
        e.move(Direction.IDLE);
    }

    void saveFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        Hashtable<Integer, Direction> table = getFloorRequests();
        if(table.get(fromFloorNumber) != direction) {
            table.put(fromFloorNumber, direction);
            EventLogger.print("Floor Request (" + fromFloorNumber + ", " + direction + ") saved.");
        }
    }
    void removeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        Hashtable<Integer, Direction> table = getFloorRequests();
        if(table.containsKey(fromFloorNumber)) {
            table.remove(fromFloorNumber, direction);
            EventLogger.print("Floor Request (" + fromFloorNumber + ", " + direction + ") removed.");
        }
    }

    void receiveFloorRequest(Message message) throws ElevatorSystemException {
        FloorRequest floorRequest = (FloorRequest) message;
        saveFloorRequest(floorRequest.getFromFloorNumber(), floorRequest.getDesiredDirection());

        controller.executeFloorRequest(getFloorRequests());
    }

    void receiveElevatorRequest(Message message) throws ElevatorSystemException {
        ElevatorRequest elevatorRequest = (ElevatorRequest) message;
        Elevator e = getElevatorById(elevatorRequest.getRequestedFromElevator());
        e.addNextStop(elevatorRequest.getRequestedDestinationFloor());
        e.move(e.getDirection());
        controller.executeElevatorRequest(e.getElevatorId(), e.getNextFloorQueue());
    }

    void receiveLocationUpdateMessage(Message message) throws ElevatorSystemException {
        controller.receiveLocationUpdateMessage(message);
    }

    public void enterRider(Message message) throws ElevatorSystemException {
        ElevatorRequest elevatorRequest = (ElevatorRequest) message;
        Elevator e = getElevatorById(elevatorRequest.getRequestedFromElevator());
        e.enterRider();
    }

    public void exitRider(int elevatorId) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        e.exitRider();
    }

    private Hashtable<Integer, Direction> getFloorRequests() {
        return floorRequests;
    }

    private void setController(Controller controller) {
        this.controller = controller;
    }

    private void setElevators() throws ElevatorSystemException {
        try {
            this.elevators = new ArrayList<>();
            int numberOfElevators = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfElevators"));
            for (int i = 1; i <= numberOfElevators; i++) {
                Elevator e = new Elevator();
                addElevator(e);
            }
        } catch(NumberFormatException nfe) {
            throw new ElevatorSystemException("Wrong configuration value for number of elevators.");
        }
    }
    private void addElevator(Elevator elevator) throws ElevatorSystemException {
        try {
            getElevators().add(elevator);
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("INTERNAL ERROR: elevators/observers list is null");
        }
    }

    private List<Elevator> getElevators() {
        return elevators;
    }
    private Elevator getElevatorById(int id) {
        for(Elevator e : getElevators()) {
            if(id == e.getElevatorId()) {
                return e;
            }
        }
        return null;
    }
    boolean requestExists(int floorNumber, Direction desiredDirection) {
        if(getFloorRequests().containsKey(floorNumber) && getFloorRequests().get(floorNumber) == desiredDirection) {
            return true;
        }
        return false;
    }
    boolean atLeastOneRequeat() {
        if(getFloorRequests().isEmpty()) {
            return false;
        }
        return true;
    }
}
