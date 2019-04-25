package elevator;

enum RiderStatus {WAITING, RIDING, DONE}

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
    public void requestElevator() throws ElevatorSystemException {//TODO: Request from Floor for an Elevator
        int destination = getDestinationFloor();
        int origin = getOriginFloor();
        if(destination == origin) return;
        Direction direction = (destination > origin) ? Direction.UP : Direction.DOWN;
        System.out.println("Person[" + getId() +  "] pressed the " + direction.toString() + " button on Floor " + getOriginFloor() + "...going " + direction.toString());
        Request request = new Request(RequestType.ELEVATOR, origin, direction);
        Building.getInstance().relayRequestToControlCenter(request);
    }

    @Override
    public void requestFloor(int elevatorId) throws  ElevatorSystemException {//TODO: Request from Elevator to go to Floor
        Direction direction = (getOriginFloor() < getDestinationFloor()) ? Direction.UP : Direction.DOWN;
        System.out.println("Person " + getId() + " pressed " + getDestinationFloor() + " in elevator " + elevatorId);
        Building.getInstance().relayRequestToControlCenter(new Request(RequestType.FLOOR, getDestinationFloor(), direction, elevatorId));
    }

    @Override
    public void boardElevator(int elevatorId) throws ElevatorSystemException {
        setStatus(RiderStatus.RIDING);
        setElevatorBoardedOn(elevatorId);
        setBoardingTime(System.nanoTime());
        System.out.println("Person [" + getId() + "] is now " + getStatus().toString() + " Elevator[" + getElevatorBoardedOn() + "]");
        //TODO: send notification of boarding... why???
        requestFloor(elevatorId);
    }

    @Override
    public void exitElevator() {
        setStatus(RiderStatus.DONE);
        setExitTime(System.nanoTime());
    }

    @Override
    public void update(ControlSignal signal) throws ElevatorSystemException  {
        //TODO: Person responds to signals of type ELEVATOR_LOCATION, ...
        if(signal.getSignalType() == ControlSignalType.ELEVATOR_LOCATION) {
            decideToBoardOrIgnoreElevator((ElevatorLocationSignal) signal);
        }
    }

    public void decideToBoardOrIgnoreElevator(ElevatorLocationSignal signal) throws ElevatorSystemException {

        int elevatorId = signal.getElevatorId();
        int floorNumber = signal.getFloorNumber();
        Direction directionOfElevator = signal.getDirection();

        int originFloor = getOriginFloor();
        int destinationFloor = getDestinationFloor();
        Direction intendedDirection = (originFloor < destinationFloor) ? Direction.UP : Direction.DOWN;

        if(getStatus() == RiderStatus.RIDING && elevatorId == getElevatorBoardedOn() && floorNumber == getDestinationFloor()) {
            exitElevator();
            return;
        }

        if(getStatus() == RiderStatus.WAITING && floorNumber == originFloor && intendedDirection == directionOfElevator) {
            //board elevator and request floor immediately.
            boardElevator(elevatorId);
            return;
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
