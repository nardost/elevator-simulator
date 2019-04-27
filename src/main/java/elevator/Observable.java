package elevator;

public interface Observable {
    void addObserver(Observer o) throws ElevatorSystemException;
    void deleteObserver(Observer o) throws ElevatorSystemException;
    void notifyObservers(Message message) throws ElevatorSystemException;
    int countObservers() throws ElevatorSystemException;
}
