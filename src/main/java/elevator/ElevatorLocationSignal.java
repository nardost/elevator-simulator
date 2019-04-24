package elevator;

public class ElevatorLocationSignal {

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
