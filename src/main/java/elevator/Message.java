package elevator;

abstract class Message {
}

class FloorRequest extends Message {

    private int fromFloorNumber;
    private Direction desiredDirection;

    public FloorRequest(int fromFloorNumber, Direction desiredDirection) throws ElevatorSystemException {

        setFromFloorNumber(fromFloorNumber);
        setDesiredDirection(desiredDirection);
    }

    public int getFromFloorNumber() {
        return fromFloorNumber;
    }

    public Direction getDesiredDirection() {
        return desiredDirection;
    }

    private void setFromFloorNumber(int fromFloorNumber) throws ElevatorSystemException {
        if(fromFloorNumber < 1 || fromFloorNumber > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor must be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
        this.fromFloorNumber = fromFloorNumber;
    }

    private void setDesiredDirection(Direction desiredDirection) {
        this.desiredDirection = desiredDirection;
    }
}

class ElevatorRequest extends Message {

    private int requestedFromElevator;
    private int requestedDestinationFloor;

    public ElevatorRequest(int requestedFromElevator, int requestedDestinationFloor) throws ElevatorSystemException {
        setRequestedFromElevator(requestedFromElevator);
        setRequestedDestinationFloor(requestedDestinationFloor);
    }

    public int getRequestedFromElevator() {
        return requestedFromElevator;
    }

    public int getRequestedDestinationFloor() {
        return requestedDestinationFloor;
    }

    private void setRequestedFromElevator(int requestedFromElevator) throws ElevatorSystemException {
        if(requestedFromElevator < 1 || requestedFromElevator > Building.getInstance().getNumberOfElevators()) {
            throw new ElevatorSystemException("Elevator must be between 1 and " + Building.getInstance().getNumberOfElevators());
        }
        this.requestedFromElevator = requestedFromElevator;
    }

    private void setRequestedDestinationFloor(int requestedDestinationFloor) throws ElevatorSystemException {
        if(requestedDestinationFloor < 1 || requestedDestinationFloor > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor must be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
        this.requestedDestinationFloor = requestedDestinationFloor;
    }
}

class LocationUpdateMessage extends Message {
    
    private int elevatorId;
    private int elevatorLocation;
    private Direction servingDirection;

    public LocationUpdateMessage(int elevatorId, int elevatorLocation, Direction servingDirection) throws ElevatorSystemException {
        setElevatorId(elevatorId);
        setElevatorLocation(elevatorLocation);
        setServingDirection(servingDirection);
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public int getElevatorLocation() {
        return elevatorLocation;
    }

    public Direction getServingDirection() {
        return servingDirection;
    }

    private void setElevatorId(int elevatorId) throws ElevatorSystemException {
        if(elevatorId < 1 || elevatorId > Building.getInstance().getNumberOfElevators()) {
            throw new ElevatorSystemException("Elevator number is only 1 through " + Building.getInstance().getNumberOfElevators());
        }
        this.elevatorId = elevatorId;
    }

    private void setElevatorLocation(int elevatorLocation) throws ElevatorSystemException {
        if(elevatorLocation < 1 || elevatorLocation > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Elevator number is only 1 through " + Building.getInstance().getNumberOfElevators());
        }
        this.elevatorLocation = elevatorLocation;
    }

    private void setServingDirection(Direction servingDirection) {
        this.servingDirection = servingDirection;
    }
}