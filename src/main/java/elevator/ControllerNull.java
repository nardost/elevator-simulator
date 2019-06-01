package elevator;

class ControllerNull implements Controller {

    @Override
    public void run() {

    }

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {
        Validator.validateFloorNumber(elevatorLocation);
        Validator.validateElevatorNumber(elevatorId);
    }

    @Override
    public void executeElevatorRequest(int elevatorId, int personId, int destinationFloorNumber, int fromFloorNumber) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateFloorNumber(destinationFloorNumber);
        Validator.validateFloorNumber(fromFloorNumber);
        Validator.validateGreaterThanZero(personId);
    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {
        Validator.validateFloorNumber(fromFloorNumber);
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateFloorNumber(elevatorLocation);
    }

    @Override
    public void exitRider(int elevatorId, int personId, int floorNumber) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateGreaterThanZero(personId);
        Validator.validateFloorNumber(floorNumber);
    }

    @Override
    public boolean pendingFloorRequests(int elevatorId) throws ElevatorSystemException {
        return false;
    }

    @Override
    public String unhandledFloorRequests() throws ElevatorSystemException {
        return "";
    }
}
