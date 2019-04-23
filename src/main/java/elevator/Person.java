package elevator;

enum RiderStatus {WAITING, RIDING, DONE}

class Person implements Rider, Observer {

    private int id;
    private int originFloor;
    private int destinationFloor;
    private long createdTime;
    private long boardingTime;
    private long exitTime;
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
        System.out.println("Person[" + getId() +  "] on Floor[" + getOriginFloor() + "] requested for an elevator.");
        Building.getInstance().relayRequestToControlCenter(new Request(RequestType.ELEVATOR, origin, direction));
    }

    @Override
    public void requestFloor(int floor) throws  ElevatorSystemException {//TODO: Request from Elevator to go to Floor
        Direction direction = (floor < getDestinationFloor()) ? Direction.UP : Direction.DOWN;
        Building.getInstance().relayRequestToControlCenter(new Request(RequestType.FLOOR, floor, direction));
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
}
