package elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ControllerBeta implements Controller {

    private List<FloorRequestFlyweight> floorRequests = Collections.synchronizedList(new ArrayList<>());

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
    public void executeFloorRequest(FloorRequestFlyweight floorRequest, int personId, long time) throws ElevatorSystemException {
        saveFloorRequest(floorRequest);
        EventLogger.print("FloorRequest " + floorRequest.toString() + " saved");
    }

    @Override
    public void executeLocationUpdate(int elevatorId, int toFloorNumber, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException {

    }

    private List<FloorRequestFlyweight> getFloorRequests() {
        return floorRequests;
    }
    private void saveFloorRequest(FloorRequestFlyweight floorRequest) {
        getFloorRequests().add(floorRequest);
    }
}
