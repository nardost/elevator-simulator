package elevator;

interface Observer {
    void update(int elevatorId, int floorNumber, Direction directionOfElevator, Direction directionDispatchedFor) throws ElevatorSystemException;
}
