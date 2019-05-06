package elevator;

interface Rider {
    void boardElevator(int id) throws ElevatorSystemException;
    void exitElevator(int elevatorId) throws ElevatorSystemException;
    int getId();
}
