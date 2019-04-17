package elevator;

interface ObservableByRiders {
    void register(Rider observer) throws ElevatorSystemException;
    void unregister(Rider observer) throws ElevatorSystemException;
    void notifyRiders();//TODO: parameters to be decided
}
