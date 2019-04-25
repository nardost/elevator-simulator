package elevator;

enum ControlSignalType {GOTO, ELEVATOR_LOCATION, RIDER_ON_BOARD}

public interface ControlSignal {
    ControlSignalType getSignalType();
}

class GotoSignal implements ControlSignal {

    private int elevatorId;
    private int gotoFloor;
    private Direction direction;

    public GotoSignal(int elevatorId, int gotoFloor, Direction direction) throws ElevatorSystemException {
        setElevatorId(elevatorId);
        setGotoFloor(gotoFloor);
        setDirection(direction);
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public int getGotoFloor() {
        return gotoFloor;
    }


    public Direction getDirection() {
        return direction;
    }

    @Override
    public ControlSignalType getSignalType() {
        return ControlSignalType.GOTO;
    }

    private void setElevatorId(int elevatorId) throws ElevatorSystemException {
        int numberOfElevatorsInBuilding = Building.getInstance().getNumberOfElevators();
        if(elevatorId < 1 || elevatorId > numberOfElevatorsInBuilding) {
            throw new ElevatorSystemException("Elevator number should be between 1 and " + numberOfElevatorsInBuilding);
        }
        this.elevatorId = elevatorId;
    }

    private void setGotoFloor(int gotoFloor) throws ElevatorSystemException {
        int numberOfFloorsInBuilding = Building.getInstance().getNumberOfFloors();
        if(gotoFloor < 1 || gotoFloor > numberOfFloorsInBuilding) {
            throw new ElevatorSystemException("Elevator number should be between 1 and " + numberOfFloorsInBuilding);
        }
        this.gotoFloor = gotoFloor;
    }

    private void setDirection(Direction direction) throws ElevatorSystemException {
        if(direction == Direction.IDLE) {
            throw new ElevatorSystemException("Direction should be UP or DOWN");
        }
        this.direction = direction;
    }
}

class ElevatorLocationSignal implements ControlSignal {

    private int elevatorId;
    private int floorNumber;
    private Direction direction;

    public ElevatorLocationSignal(int elevatorId, int floorNumber, Direction direction) throws ElevatorSystemException {
        setElevatorId(elevatorId);
        setFloorNumber(floorNumber);
        setDirection(direction);
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public ControlSignalType getSignalType() {
        return ControlSignalType.ELEVATOR_LOCATION;
    }

    private void setElevatorId(int elevatorId) throws ElevatorSystemException {
        int numberOfElevatorsInBuilding = Building.getInstance().getNumberOfElevators();
        if(elevatorId < 1 || elevatorId > numberOfElevatorsInBuilding) {
            throw new ElevatorSystemException("Elevator number should be between 1 and " + numberOfElevatorsInBuilding);
        }
        this.elevatorId = elevatorId;
    }

    private void setFloorNumber(int floorNumber) throws ElevatorSystemException {
        int numberOfFloorsInBuilding = Building.getInstance().getNumberOfFloors();
        if(floorNumber < 1 || floorNumber > numberOfFloorsInBuilding) {
            throw new ElevatorSystemException("Elevator number should be between 1 and " + numberOfFloorsInBuilding);
        }
        this.floorNumber = floorNumber;
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }
}

class RiderOnBoardSignal implements ControlSignal {

    private int elevatorId;
    private int destinationFloor;

    public RiderOnBoardSignal(int elevatorId, int destinationFloor) {

        setElevatorId(elevatorId);
        setDestinationFloor(destinationFloor);
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public void setElevatorId(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    @Override
    public ControlSignalType getSignalType() {
        return ControlSignalType.RIDER_ON_BOARD;
    }

    private void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }
}


