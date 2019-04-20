package elevator;

interface Rider {
    void requestElevator() throws ElevatorSystemException;
    void requestFloor(int floor);
    void boardElevator(int id);
    void exitElevator();
    int getId();
}
