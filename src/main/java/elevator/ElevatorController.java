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

    void removeFloorRequest(Message message, int elevatorBoardedOn) throws ElevatorSystemException {
        FloorRequest floorRequest = (FloorRequest) message;
        int originFloor = floorRequest.getFromFloorNumber();
        Direction directionRequested = floorRequest.getDesiredDirection();
        controller.removeFloorRequest(originFloor, directionRequested, elevatorBoardedOn);
    }

    void receiveFloorRequest(Message message) throws ElevatorSystemException {
        FloorRequest floorRequest = (FloorRequest) message;
        int fromFloorNumber = floorRequest.getFromFloorNumber();
        Direction desiredDirection = floorRequest.getDesiredDirection();
        controller.saveFloorRequest(fromFloorNumber, desiredDirection);
    }

    void receiveElevatorRequest(Message message) throws ElevatorSystemException {
        ElevatorRequest elevatorRequest = (ElevatorRequest) message;
        int elevatorId = elevatorRequest.getRequestedFromElevator();
        int floorNumber = elevatorRequest.getRequestedDestinationFloor();
        controller.executeElevatorRequest(elevatorId, floorNumber);

    }

    void receiveLocationUpdateMessage(Message message) throws ElevatorSystemException {
        LocationUpdateMessage lum = (LocationUpdateMessage) message;
        controller.executeLocationUpdate(lum.getElevatorId(), lum.getElevatorLocation(), lum.getServingDirection());
    }

    Elevator getElevatorById(int id) {
        for(Elevator e : getElevators()) {
            if(id == e.getElevatorId()) {
                return e;
            }
        }
        return null;
    }

    //void enterRider(Message message) throws ElevatorSystemException {
    void enterRider(int origin, int destination, int elevatorBoardedOn) throws ElevatorSystemException {
        Direction direction = (origin < destination) ? Direction.UP : Direction.DOWN;
        //ElevatorRequest elevatorRequest = (ElevatorRequest) message;
        Elevator e = getElevatorById(elevatorBoardedOn);
        e.enterRider();
        controller.removeFloorRequest(origin, direction, elevatorBoardedOn);

    }

    void exitRider(int elevatorId, int floorNumber) throws ElevatorSystemException {
        Elevator e = getElevatorById(elevatorId);
        if(e.peekNextStop() != null && floorNumber != e.peekNextStop()){
            throw new ElevatorSystemException("floor number (" + floorNumber + ") should have been the same as next stop of elevator ( " + e.peekNextStop() + ").");
        }
        e.pollNextStop();
        e.exitRider(floorNumber);
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
