package elevator;

public interface Observable {
    void addObserver(Observer o) throws ElevatorSystemException;
    void notifyObservers(int elevatorId, int elevatorLocation, Direction direction, Direction directionDispatchedFor) throws ElevatorSystemException;
    int countObservers() throws ElevatorSystemException;
}
