package elevator;

interface Rider {
    void requestElevator() throws ElevatorSystemException;
    void requestFloor(int floor) throws  ElevatorSystemException;
    void boardElevator(int id) throws ElevatorSystemException;
    void exitElevator();
    int getId();
}
