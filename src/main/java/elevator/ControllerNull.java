package elevator;

import java.util.HashMap;

class ControllerNull implements Controller {

    @Override
    public void selectElevatorAndSendToFloor(int forFloorNumber, Direction desiredDirection) throws ElevatorSystemException {

    }

    @Override
    public void announceLocationOfElevator(int elevatorId, int elevatorLocation, Direction elevatorServingDirection) throws ElevatorSystemException {

    }


    @Override
    public void executeElevatorRequest(int elevatorId, int floorNumber) throws ElevatorSystemException {

    }

    @Override
    public void executeFloorRequest() throws ElevatorSystemException {

    }

    @Override
    public void executeLocationUpdate(int elevatorId, int toFloorNumber, Direction direction) throws ElevatorSystemException {

    }

    @Override
    public void saveFloorRequest(int floorNumber, Direction direction) throws ElevatorSystemException {

    }

    @Override
    public void removeFloorRequest(int fromFloorNumber, Direction directionRequested, int elevatorBoardedOn) throws ElevatorSystemException {

    }
}
