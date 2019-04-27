package elevator;

interface Rider {
    void sendMeAnElevator() throws ElevatorSystemException;
    void takeMeToMyDestination(int floor) throws  ElevatorSystemException;
    void boardElevator(int id) throws ElevatorSystemException;
    void exitElevator(int elevatorId) throws ElevatorSystemException;
    int getId();
}
