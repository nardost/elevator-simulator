package elevator;

public class GotoSignal implements ControlSignal {

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
