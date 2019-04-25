package elevator;

public class RiderOnBoardSignal implements ControlSignal {

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
