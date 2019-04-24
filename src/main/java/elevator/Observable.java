package elevator;

public interface Observable {
    void addObserver(Observer o) throws ElevatorSystemException;
    void deleteObserver(Observer o) throws ElevatorSystemException;
    void notifyObservers(GotoSignal signal) throws ElevatorSystemException;
    void notifyObservers(ElevatorLocationSignal signal) throws ElevatorSystemException;
    void notifyObservers(Signal signal) throws ElevatorSystemException;
    int countObservers() throws ElevatorSystemException;
}
