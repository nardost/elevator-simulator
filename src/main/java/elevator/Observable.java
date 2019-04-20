package elevator;

public interface Observable {
    void addObserver(Observer o);
    void deleteObserver(Observer o);
    void notifyObservers(Signal signal);
    int countObservers();
}
