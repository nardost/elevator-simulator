package elevator;

import java.time.Instant;
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
        setId(++instanceCounter);
        setOriginFloor(origin);
        setDestinationFloor(destination);
        setCreatedTime(System.nanoTime());
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
        setBoardingTime(System.nanoTime());
        Building.getInstance().relayElevatorRequestToControlCenter(elevatorId, destination, origin, getId());
    }

    @Override
    public void exitElevator(int elevatorId) throws ElevatorSystemException {
        setStatus(RiderStatus.DONE);
        setExitTime(System.nanoTime());
        Building.getInstance().relayExitRiderFromElevatorMessage(elevatorId, getDestinationFloor(), getId());
    }

    @Override
    public void update(int elevatorId, int floorNumber, Direction directionOfElevator, Direction directionDispatchedFor) throws ElevatorSystemException  {
        decideToBoardOrIgnoreOrExitElevator(elevatorId, floorNumber, directionOfElevator, directionDispatchedFor);
    }

    public void decideToBoardOrIgnoreOrExitElevator(int elevatorId, int floorNumber, Direction directionOfElevator, Direction directionDispatchedFor) throws ElevatorSystemException {

        int originFloor = getOriginFloor();
        int destinationFloor = getDestinationFloor();
        Direction intendedDirection = (originFloor < destinationFloor) ? Direction.UP : Direction.DOWN;

        if(getStatus() == RiderStatus.RIDING && elevatorId == getElevatorBoardedOn() && floorNumber == destinationFloor) {
            exitElevator(elevatorId);
            return;
        }

        if(getStatus() == RiderStatus.WAITING) {
            if(floorNumber == originFloor) {
                if(intendedDirection == directionOfElevator) {// || intendedDirection == directionDispatchedFor) {
                    boardElevator(elevatorId);
                }
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public RiderStatus getStatus() {
        return status;
    }

    public int getOriginFloor() {
        return originFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getBoardingTime() {
        return boardingTime;
    }

    public long getExitTime() {
        return exitTime;
    }

    public int getElevatorBoardedOn() {
        return elevatorBoardedOn;
    }

    private void setId(int id) {
        this.id = id;
    }

    public void setStatus(RiderStatus status) {
        this.status = status;
    }

    private void setOriginFloor(int origin) {
        this.originFloor = origin;
    }

    private void setDestinationFloor(int destinationFloor) throws ElevatorSystemException {
        if(destinationFloor > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor should be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
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
        int numberOfElevatorsInBuilding = Building.getInstance().getNumberOfElevators();
        if(elevatorBoardedOn < 1 || elevatorBoardedOn > numberOfElevatorsInBuilding) {
            throw new ElevatorSystemException("Elevator should be between 1 and " + numberOfElevatorsInBuilding);
        }
        this.elevatorBoardedOn = elevatorBoardedOn;
    }
}
