package elevator;

interface Rider {
    void requestElevator(Direction upOrDown);
    void requestDestinationFloor(int floor);
    void boardElevator(int id);
    void exitElevator();
    String getId();
}
