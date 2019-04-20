package elevator;

enum PayloadType {
    CONTROLLER_TO_FLOORS__LOCATION_OF_ALL_ELEVATORS,
    CONTROLLER_TO_ELEVATOR__GOTO_FLOOR_DIRECTION,
    FLOOR_TO_WAITING_PERSONS__ELEVATOR_ARRIVAL,
    ELEVATOR_TO_RIDING_PERSONS__ARRIVAL_TO_FLOOR,
    ELEVATOR_TO_RIDING_PERSONS__DOOR_OPEN_CLOSED,
    ELEVATOR_TO_RIDING_PERSONS__GOING_UP_DOWN
}
public class Payload {
    private PayloadType payloadType;
    private int elevatorNumber;
    private int floorNumber;
    private Direction direction;
    private boolean doorsClosed;

    public Payload(PayloadType payloadType, int elevatorNumber, int floorNumber, Direction direction, boolean doorsClosed)
            throws ElevatorSystemException {
        setPayloadType(payloadType);
        setElevatorNumber(elevatorNumber);
        setFloorNumber(floorNumber);
        setDirection(direction);
        setDoorsClosed(doorsClosed);
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public int getElevatorNumber() {
        return elevatorNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean areDoorsClosed() {
        return doorsClosed;
    }

    private void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    private void setElevatorNumber(int elevatorNumber) throws ElevatorSystemException {
        if(elevatorNumber < 1 || elevatorNumber > Building.getInstance().getNumberOfElevators()) {
            throw new ElevatorSystemException("Elevator number is only 1 through " + Building.getInstance().getNumberOfElevators());
        }
        this.elevatorNumber = elevatorNumber;
    }

    private void setFloorNumber(int floorNumber) throws ElevatorSystemException {
        if(floorNumber < 1 || floorNumber > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor number is only 1 through " + Building.getInstance().getNumberOfFloors());
        }
        this.floorNumber = floorNumber;
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void setDoorsClosed(boolean doorsClosed) {
        this.doorsClosed = doorsClosed;
    }

}
