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
        System.out.println("Person " + getId() + " clicked the " + getDestinationFloor() + " button in elevator " + elevatorId);
        Building.getInstance().relayRequestToControlCenter(new Request(RequestType.FLOOR, getDestinationFloor(), direction, elevatorId));
    }

    @Override
    public void boardElevator(int id) throws ElevatorSystemException {
        Building.getInstance().addRiderToElevator(id, this);
        setStatus(RiderStatus.RIDING);
        setBoardingTime(System.nanoTime());
    }

    @Override
    public void exitElevator() {
        setStatus(RiderStatus.DONE);
        setExitTime(System.nanoTime());
    }

    @Override
    public void update(GotoSignal signal) throws ElevatorSystemException  {//TODO: Floor updates Person with Signal. Person acts on signal
        //signal intended for elevators. do nothing.
        System.out.println("GotoSignal is for Elevators. I am a person...");
    }

    @Override
    public void update(ElevatorLocationSignal signal) throws ElevatorSystemException  {//TODO: Floor updates Person with Signal. Person acts on signal

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
            System.out.println("Person [" + getId() + "] status = " + getStatus().toString());
            setStatus(RiderStatus.RIDING);
            System.out.println("Person [" + getId() + "] status = " + getStatus().toString());
            setBoardingTime(System.nanoTime());
            requestFloor(elevatorId);
            return;
        }
    }

    @Override
    public void update(Signal signal) throws ElevatorSystemException  {//TODO: Floor updates Person with Signal. Person acts on signal
        if(signal.getReceiver() == ElementType.ALL || (signal.getReceiver() == ElementType.PERSON)) {
            if(signal.getPayloadType() == PayloadType.FLOOR_TO_WAITING_PERSONS__ELEVATOR_ARRIVAL) {

                int elevatorNumber = signal.getField1();
                Direction directionOfElevator = signal.getField4();

                System.out.println("Person[" + getId() + "] received signal [" + signal.getPayloadType().toString() + "]");

                //TODO: if the direction is the same as mine, i will board it.
                Direction intendedDirection = (getOriginFloor() < getDestinationFloor()) ? Direction.UP : Direction.DOWN;
                System.out.println("Intended Direction = " + intendedDirection.toString() + ": Elevator Direction = " + directionOfElevator.toString());
                if (intendedDirection == directionOfElevator) {
                    System.out.println("Person[" + getId() + "] boarding elevator[" + elevatorNumber + "]");
                    boardElevator(elevatorNumber);
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
