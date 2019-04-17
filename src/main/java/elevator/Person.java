package elevator;

import java.util.List;

enum Direction {UP, DOWN}

class Person implements Rider, Observer {

    private String id;
    private Elevator currentElevator;
    private Floor floor;
    private List<Elevator> elevators;
    private EventLogger logger;

    public Person(String id, Floor floor) throws ElevatorSystemException {
        this.id = id;
        this.floor = floor;
        this.logger = EventLogger.getInstance("FILE");
    }

    @Override
    public void requestElevator(Direction direction) {
        //TODO: request elevator
        Signal signal = new Signal(Receiver.BUILDING, 0, floor.getId());
        floor.notifyController(signal);
    }

    @Override
    public void requestDestinationFloor(int floor) {
        //this.elevator.queueRequest(floor)
    }

    @Override
    public void boardElevator(int id) {
        for(Elevator elevator : elevators) {
            if(elevator.getId() == id) {
                elevator.enterRider(this);
                currentElevator = elevator;
            }
        }
    }

    @Override
    public void exitElevator() {
        currentElevator.exitRider(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update(Signal signal) {

    }
}
