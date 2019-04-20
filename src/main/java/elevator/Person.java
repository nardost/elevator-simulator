package elevator;

class Person implements Rider, Observer {

    private int id;
    private int originFloor;
    private int destinationFloor;
    private Elevator currentElevator;
    //TODO: timing info

    private static  int instanceCounter = 0;

    public Person(int origin, int destination) throws ElevatorSystemException {
        setId(++instanceCounter);
        setOriginFloor(origin);
        setDestinationFloor(destination);
        setCurrentElevator(null);
    }

    @Override
    public void requestElevator() throws ElevatorSystemException {//TODO: Request from Floor for an Elevator
        int destination = getDestinationFloor();
        int origin = getOriginFloor();
        if(destination == origin) return;
        Direction direction = (destination > origin) ? Direction.UP : Direction.DOWN;
        Request floorRequest = new Request(destination, direction);
        Building.getInstance().relayRequestToControlCenter(floorRequest);
    }

    @Override
    public void requestFloor(int floor) {//TODO: Request from Elevator to go to Floor
        //this.elevator.queueRequest(floor)
    }

    @Override
    public void boardElevator(int id) {

    }

    @Override
    public void exitElevator() {
        currentElevator.exitRider(this);
    }

    @Override
    public void update(Signal signal) {//TODO: Floor updates Person with Signal. Person acts on signal
        if(signal.getReceiver() == ElementType.PERSON && signal.getReceiverId() == getId()) {
            //this signal is destined for me. I will act ont it.
        }
    }

    @Override
    public int getId() {
        return id;
    }

    private int getOriginFloor() {
        return originFloor;
    }

    private int getDestinationFloor() {
        return destinationFloor;
    }

    private Elevator getCurrentElevator() {
        return currentElevator;
    }

    private void setId(int id) {
        this.id = id;
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

    private void setCurrentElevator(Elevator currentElevator) {//can be null
        this.currentElevator = currentElevator;
    }
}
