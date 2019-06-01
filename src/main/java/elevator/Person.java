package elevator;

import java.util.Objects;

class Person implements Rider, Observer {

    private int id;
    private int originFloor;
    private int destinationFloor;
    private long createdTime;
    private long boardingTime;
    private long exitTime;
    private int elevatorBoardedOn;
    private RiderStatus status;

    private static  int instanceCounter = 0;

    public Person(int origin, int destination) throws ElevatorSystemException {
        Validator.validateFloorNumber(origin);
        Validator.validateFloorNumber(destination);
        setId(++instanceCounter);
        setOriginFloor(origin);
        setDestinationFloor(destination);
        setCreatedTime(System.currentTimeMillis());
        setStatus(RiderStatus.WAITING);
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof Person) {
            if(getId() == ((Person) object).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void sendMeAnElevator() throws ElevatorSystemException {
        int destination = getDestinationFloor();
        int origin = getOriginFloor();
        Direction direction = (destination > origin) ? Direction.UP : Direction.DOWN;
        if(destination == origin) {
            EventLogger.print("Person P" + getId() + " does not need an elevator.");
            setBoardingTime(getCreatedTime());
            setExitTime(getCreatedTime());
            setStatus(RiderStatus.DONE);
            return;
        }
        EventLogger.print("Person P" + getId() + " presses " + direction.toString() + " on Floor " + origin);
        Building.getInstance().relayFloorRequestToControlCenter(origin, direction);
    }

    @Override
    public void boardElevator(int elevatorId) throws ElevatorSystemException {
        int destination = getDestinationFloor();
        int origin = getOriginFloor();
        setStatus(RiderStatus.RIDING);
        setElevatorBoardedOn(elevatorId);
        setBoardingTime(System.currentTimeMillis());
        Building.getInstance().relayElevatorRequestToControlCenter(elevatorId, destination, origin, getId());
    }

    @Override
    public void exitElevator(int elevatorId) throws ElevatorSystemException {
        setStatus(RiderStatus.DONE);
        setExitTime(System.currentTimeMillis());
        Building.getInstance().relayExitRiderFromElevatorMessage(elevatorId, getDestinationFloor(), getId());
    }

    @Override
    public void update(int elevatorId, int floorNumber, Direction directionOfElevator, Direction directionDispatchedFor) throws ElevatorSystemException  {
        decideToBoardOrIgnoreOrExitElevator(elevatorId, floorNumber, directionOfElevator, directionDispatchedFor);
    }

    public void decideToBoardOrIgnoreOrExitElevator(int elevatorId, int floorNumber, Direction directionOfElevator, Direction directionDispatchedFor) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorId);
        Validator.validateFloorNumber(floorNumber);
        int originFloor = getOriginFloor();
        int destinationFloor = getDestinationFloor();
        Direction intendedDirection = (originFloor < destinationFloor) ? Direction.UP : Direction.DOWN;

        if(getStatus() == RiderStatus.RIDING) {
            if(elevatorId == getElevatorBoardedOn() && floorNumber == destinationFloor) {
                exitElevator(elevatorId);
            } else {
                StringBuilder sb = new StringBuilder();
                String reasonForNotExiting = (elevatorId != getElevatorBoardedOn()) ? " I am not on that elevator.": "";
                sb.append(reasonForNotExiting);
                reasonForNotExiting = (floorNumber != destinationFloor) ? " This is not my destination." : "";
                sb.append(reasonForNotExiting);
                EventLogger.print("P" + getId() + " on Floor " + floorNumber + " can't get out of Elevator " + elevatorId + " because " + sb.toString());
            }
        }

        if(getStatus() == RiderStatus.WAITING) {
            if(floorNumber == originFloor) {
                if(intendedDirection == directionOfElevator || intendedDirection == directionDispatchedFor) {
                    boardElevator(elevatorId);
                }
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    RiderStatus getStatus() {
        return status;
    }

    int getOriginFloor() {
        return originFloor;
    }

    int getDestinationFloor() {
        return destinationFloor;
    }

    long getCreatedTime() {
        return createdTime;
    }

    long getBoardingTime() {
        return boardingTime;
    }

    long getExitTime() {
        return exitTime;
    }

    int getElevatorBoardedOn() {
        return elevatorBoardedOn;
    }

    private void setId(int id) {
        this.id = id;
    }

    void setStatus(RiderStatus status) {
        this.status = status;
    }

    private void setOriginFloor(int origin) {
        this.originFloor = origin;
    }

    private void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }


    private void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    private void setBoardingTime(long boardingTime) {
        this.boardingTime = boardingTime;
    }

    private void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    private void setElevatorBoardedOn(int elevatorBoardedOn) throws ElevatorSystemException {
        Validator.validateElevatorNumber(elevatorBoardedOn);
        this.elevatorBoardedOn = elevatorBoardedOn;
    }
}
