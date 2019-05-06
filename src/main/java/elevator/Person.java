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

    @Override
    public void sendMeAnElevator() throws ElevatorSystemException {
        int destination = getDestinationFloor();
        int origin = getOriginFloor();
        Direction direction = (destination > origin) ? Direction.UP : Direction.DOWN;

        if(destination == origin) {
            return;
        }

        EventLogger.print("P" + getId() +  " pressed " + direction.toString() + " on F" + getOriginFloor() + ". Going " + direction.toString() + " to F" + getDestinationFloor() + ".");
        Message floorRequest = new FloorRequest(getOriginFloor(), direction);
        Building.getInstance().relayFloorRequestToControlCenter(floorRequest);
    }

    @Override
    public void takeMeToMyDestination(int elevatorId) throws  ElevatorSystemException {
        Direction direction = (getOriginFloor() < getDestinationFloor()) ? Direction.UP : Direction.DOWN;
        EventLogger.print("P" + getId() + " pressed " + getDestinationFloor() + " in E" + elevatorId);
        Message elevatorRequest = new ElevatorRequest(elevatorId, getDestinationFloor());
        Building.getInstance().relayElevatorRequestToControlCenter(elevatorRequest);
    }

    @Override
    public void boardElevator(int elevatorId) throws ElevatorSystemException {
        int destination = getDestinationFloor();
        int origin = getOriginFloor();
        Direction direction = (destination > origin) ? Direction.UP : Direction.DOWN;

        setStatus(RiderStatus.RIDING);
        setElevatorBoardedOn(elevatorId);
        setBoardingTime(System.nanoTime());
        EventLogger.print("P" + getId() + " on F" + origin + " boarded E" + getElevatorBoardedOn() + " and is going " + direction.toString() + " to F" + destination);
        Message elevatorRequest = new ElevatorRequest(elevatorId, getDestinationFloor());
        Message floorRequest = new FloorRequest(getOriginFloor(), direction);
        //Building.getInstance().relayDeleteFloorRequestMessage(floorRequest, elevatorId);
        //Building.getInstance().relayEnterRiderIntoElevatorMessage(elevatorRequest);
        Building.getInstance().relayEnterRiderIntoElevatorMessage(origin, destination, elevatorId);
        takeMeToMyDestination(elevatorId);
    }

    @Override
    public void exitElevator(int elevatorId) throws ElevatorSystemException {
        setStatus(RiderStatus.DONE);
        setExitTime(System.nanoTime());
        EventLogger.print("P" + getId() + " exits E" + elevatorId + " on F" + getDestinationFloor());
        Building.getInstance().relayExitRiderFromElevatorMessage(elevatorId, getDestinationFloor());
    }

    @Override
    public void update(Message message) throws ElevatorSystemException  {
        LocationUpdateMessage locationMessage = (LocationUpdateMessage) message;
        decideToBoardOrIgnoreOrExitElevator(locationMessage);
    }

    public void decideToBoardOrIgnoreOrExitElevator(LocationUpdateMessage message) throws ElevatorSystemException {

        int elevatorId = message.getElevatorId();
        int floorNumber = message.getElevatorLocation();
        Direction directionOfElevator = message.getServingDirection();

        int originFloor = getOriginFloor();
        int destinationFloor = getDestinationFloor();
        Direction intendedDirection = (originFloor < destinationFloor) ? Direction.UP : Direction.DOWN;

        if(getStatus() == RiderStatus.RIDING && elevatorId == getElevatorBoardedOn() && floorNumber == destinationFloor) {
            exitElevator(elevatorId);
            return;
        }

        if(getStatus() == RiderStatus.WAITING) {
            if(floorNumber == originFloor /*&& intendedDirection == directionOfElevator*/ ) {
                //board elevator and request floor immediately.
                boardElevator(elevatorId);
                return;
            } else {
                EventLogger.print("Elevator is here but chose to ignore it...");
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
