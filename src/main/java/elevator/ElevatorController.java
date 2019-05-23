package elevator;

/**
 * @author ntessema
 *
 * Context. The Strategy is Controller
 *
 */

class ElevatorController {

    private Controller controller;

    private static ElevatorController controlCenter = null;

    private ElevatorController() throws ElevatorSystemException {
        SystemConfiguration.initializeSystemConfiguration();
        setController(ControllerFactory.createController());
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


    void run() throws ElevatorSystemException {
        controller.run();
    }

    void receiveFloorRequest(int fromFloorNumber, Direction desiredDirection) throws ElevatorSystemException {
        controller.executeFloorRequest(fromFloorNumber, desiredDirection);
    }

    void receiveElevatorRequest(int elevatorId, int destinationFloor, int originFloor, int personId) throws ElevatorSystemException {
        controller.executeElevatorRequest(elevatorId, personId, destinationFloor, originFloor);

    }

    void receiveLocationUpdateMessage(int elevatorId, int location, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        controller.executeLocationUpdate(elevatorId, location, direction, directionDispatchedFor);
    }

    void exitRider(int elevatorId, int floorNumber, int personId) throws ElevatorSystemException {
        controller.exitRider(elevatorId, personId, floorNumber);
    }
    private void setController(Controller controller) {
        this.controller = controller;
    }
}
