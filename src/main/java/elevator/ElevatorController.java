package elevator;


import java.util.ArrayList;
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

    void receiveFloorRequest(int fromFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        controller.executeFloorRequest(fromFloorNumber, desiredDirection);
    }

    void receiveElevatorRequest(int elevatorId, int destinationFloor, int originFloor) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        e.enterRider();
        controller.executeElevatorRequest(elevatorId, destinationFloor, originFloor);

    }

    void receiveLocationUpdateMessage(int elevatorId, int location, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        controller.executeLocationUpdate(elevatorId, location, direction, directionDispatchedFor);
    }

    void exitRider(int elevatorId, int floorNumber) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        if(e.peekNextStop() != null && floorNumber != e.peekNextStop()){
            //throw new ElevatorSystemException("floor number (" + floorNumber + ") should have been the same as next stop of elevator ( " + e.peekNextStop() + ").");
        }
        e.exitRider();
    }

    Elevator getElevatorById(int id) {
        for(Elevator e : getElevators()) {
            if(id == e.getElevatorId()) {
                return e;
            }
        }
        return null;
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
}
