package elevator;

class ControllerNull implements Controller {

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction nowGoingInDirection, Direction directionDispatchedFor) throws ElevatorSystemException {

    }


    @Override
    public void executeElevatorRequest(int elevatorId, int destinationFloorNumber, int fromFloorNumber) throws ElevatorSystemException {

    }

    @Override
    public void executeFloorRequest(int fromFloorNumber, Direction direction) throws ElevatorSystemException {

    }

    @Override
    public void executeLocationUpdate(int elevatorId, int toFloorNumber, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {

    }
}
